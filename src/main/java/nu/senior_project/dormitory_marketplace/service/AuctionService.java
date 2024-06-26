package nu.senior_project.dormitory_marketplace.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.auction.AuctionDto;
import nu.senior_project.dormitory_marketplace.dto.lot.LotDto;
import nu.senior_project.dormitory_marketplace.dto.lot.LotFullDto;
import nu.senior_project.dormitory_marketplace.dto.post.PostShortDto;
import nu.senior_project.dormitory_marketplace.entity.Auction;
import nu.senior_project.dormitory_marketplace.entity.Lot;
import nu.senior_project.dormitory_marketplace.enums.EAuctionStatus;
import nu.senior_project.dormitory_marketplace.exception.auction.AuctionException;
import nu.senior_project.dormitory_marketplace.job.AuctionFinishJob;
import nu.senior_project.dormitory_marketplace.job.AuctionInitiationJob;
import nu.senior_project.dormitory_marketplace.repository.ApplicationRepository;
import nu.senior_project.dormitory_marketplace.repository.AuctionRepository;
import nu.senior_project.dormitory_marketplace.repository.LotRepository;
import nu.senior_project.dormitory_marketplace.repository.PostRepository;
import org.quartz.*;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final EntityConverterService entityConverterService;
    private final AuctionRepository auctionRepository;
    private final Scheduler scheduler;
    private final LotRepository lotRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public void create(AuctionDto auctionDto)  {
        Auction auction = entityConverterService.toAuction(auctionDto);
        auction.setStatus(EAuctionStatus.PLANNED);
        auctionRepository.save(auction);

        generateTimingJobs(auction);
    }

    public void initiateAuction(Long auctionId) {
        checkAuctionInitiationConditions();

        Auction auction = auctionRepository.findByIdOrThrow(auctionId);
        auction.setStatus(EAuctionStatus.INITIATED);
        auctionRepository.save(auction);
    }

    private void checkAuctionInitiationConditions() {
        List<Auction> activeAuctions = auctionRepository.findByStatusIn(List.of(EAuctionStatus.INITIATED, EAuctionStatus.ACTIVE));
        if (!activeAuctions.isEmpty())
            throw new AuctionException(
                    String.format(
                            "There are active auctions: %s",
                            activeAuctions.stream().map(Auction::getName).collect(Collectors.joining(","))
                            )
            );
    }

    private void generateTimingJobs(Auction auction) {
        scheduleStartJob(auction);
        scheduleEndJob(auction);
    }

    private JobDataMap generateJobDataMap(Auction auction) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("auctionRepository", auctionRepository);
        jobDataMap.put("lotRepository", lotRepository);
        jobDataMap.put("applicationRepository", applicationRepository);
        jobDataMap.put("auctionId", auction.getId());

        return jobDataMap;
    }

    @SneakyThrows
    private void rescheduleStartJob(Auction auction) {
        scheduler.unscheduleJob(new TriggerKey("auction_initiation_trigger_" + auction.getId()));
        scheduleStartJob(auction);
    }
    @SneakyThrows
    private void rescheduleEndJob(Auction auction) {
        scheduler.unscheduleJob(new TriggerKey("auction_finish_trigger_" + auction.getId()));
        scheduleStartJob(auction);
    }

    @SneakyThrows
    private void scheduleStartJob(Auction auction) {
        JobDetail startJob = JobBuilder.newJob(AuctionInitiationJob.class)
                .withIdentity("auction_initiation_job_" + auction.getId())
                .usingJobData(generateJobDataMap(auction))
                .build();

        Trigger startTrigger = TriggerBuilder.newTrigger()
                .withIdentity("auction_initiation_trigger_" + auction.getId())
                .startAt(auction.getStartTime())
                .build();
        scheduler.scheduleJob(startJob, startTrigger);
    }


    @SneakyThrows
    private void scheduleEndJob(Auction auction) {
        JobDetail endJob = JobBuilder.newJob(AuctionFinishJob.class)
                .withIdentity("auction_finish_job_" + auction.getId())
                .usingJobData(generateJobDataMap(auction))
                .build();

        Trigger endTrigger = TriggerBuilder.newTrigger()
                .withIdentity("auction_finish_trigger_" + auction.getId())
                .startAt(auction.getEndTime())
                .build();

        scheduler.scheduleJob(endJob, endTrigger);
    }


    @Transactional
    public void update(Long id, AuctionDto auctionDto) {
        Auction auction = auctionRepository.findByIdOrThrow(id);

        if (auctionDto.getStartTime() != null && !auction.getStartTime().equals(auctionDto.getStartTime())) {
            auction.setStartTime(auctionDto.getStartTime());
            rescheduleStartJob(auction);
            auctionRepository.save(auction);
        }

        if (auctionDto.getEndTime() != null && !auction.getEndTime().equals(auctionDto.getEndTime())) {
            auction.setEndTime(auctionDto.getEndTime());
            rescheduleEndJob(auction);
            auctionRepository.save(auction);
        }

        auction.setName(auctionDto.getName());
        auctionRepository.save(auction);
    }


    @Transactional
    public void delete(Long id) {
        auctionRepository.delete(auctionRepository.findByIdOrThrow(id));
    }

    @Transactional
    public AuctionDto get(Long id) {
        return entityConverterService.toAuctionDto(auctionRepository.findByIdOrThrow(id));
    }

    @Transactional
    public GeneralPageableResponse<AuctionDto> getList(Integer limit, Integer offset) {
        List<AuctionDto> totalResults =  auctionRepository.findAllByOrderByIdDesc()
                .stream()
                .map(entityConverterService::toAuctionDto)
                .toList();

        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }

    @Transactional
    public GeneralPageableResponse<LotFullDto> getLots(Long id, Integer limit, Integer offset) {
        Auction auction = auctionRepository.findByIdOrThrow(id);
        List<LotFullDto> totalResults =  lotRepository.findByAuctionOrderByIdDesc(auction)
                .stream()
                .map(entityConverterService::toLotFullDto)
                .toList();

        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }

    public AuctionDto getActiveAuction() {
        List<Auction> auctions = auctionRepository.findByStatusIn(List.of(EAuctionStatus.ACTIVE));

        if (auctions.isEmpty())
            return null;

        if (auctions.size() != 1) {
            throw new AuctionException("More than one auction is active, contact admins");
        }

        return entityConverterService.toAuctionDto(auctions.get(0));
    }

    public List<AuctionDto> getActiveAuctions() {
        return auctionRepository.findByStatusIn(List.of(EAuctionStatus.INITIATED))
                .stream()
                .map(entityConverterService::toAuctionDto)
                .toList();
    }
}
