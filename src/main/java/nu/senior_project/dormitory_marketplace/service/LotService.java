package nu.senior_project.dormitory_marketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.lot.LotDto;
import nu.senior_project.dormitory_marketplace.dto.lot.LotFullDto;
import nu.senior_project.dormitory_marketplace.entity.*;
import nu.senior_project.dormitory_marketplace.enums.*;
import nu.senior_project.dormitory_marketplace.exception.auction.AuctionException;
import nu.senior_project.dormitory_marketplace.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LotService {

    private final EntityConverterService entityConverterService;
    private final LotRepository lotRepository;
    private final PostRepository postRepository;
    private final AuctionRepository auctionRepository;
    private final ApplicationRepository applicationRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final Long DAY_MILLIS = 24L * 60 * 60 * 1000;

    @Transactional
    public void create(LotDto lotDto, HttpServletRequest request) {
        Post post = postRepository.findByIdOrThrow(lotDto.getPostId());
        Auction auction = auctionRepository.findByIdOrThrow(lotDto.getAuctionId());

        checkAuctionStatus(auction);
        checkPostEligibility(post);

        Lot lot  = entityConverterService.toLot(lotDto);
        lot.setAuction(auction);
        lot.setStatus(ELotStatus.INITIATED);
        lot.setPost(post);

        String username = request.getUserPrincipal().getName();
        User currentUser = userRepository.findByUsernameOrThrow(username);
        lot.setCreator(currentUser);

        lotRepository.save(lot);
    }

    private void checkPostEligibility(Post post) {
        if (!post.getProductType().equals(EProductType.SINGLE))
            throw new AuctionException("only single type of posts can participate");
        List<Lot> activeLots = lotRepository.findByPostAndStatusIn(
                post,
                Arrays.asList(
                        ELotStatus.ACTIVE,
                        ELotStatus.INITIATED,
                        ELotStatus.FINISHED,
                        ELotStatus.SALE
                )
        );

        if (!activeLots.isEmpty())
            throw new AuctionException("there is an active lot associated with the post.");

        List<Sale> sales =  saleRepository.findByPostAndStatusIn(post, Arrays.asList(
                ESaleStatus.NEEDS_SELLER_VALIDATION.getValue(),
                ESaleStatus.NEEDS_BUYER_VALIDATION.getValue()
        ));

        if (!sales.isEmpty())
            throw new AuctionException("There is an active sale. Please finish it and retry.");

    }

    private void checkAuctionStatus(Auction auction) {
        if (!auction.getStatus().equals(EAuctionStatus.INITIATED))
            throw new AuctionException("action cannot be performed under this auction status");
    }

    private void checkLotStatus(Lot lot, ELotStatus status) {
        if (!lot.getStatus().equals(status))
            throw new AuctionException("action cannot be performed under this auction status");
    }

    @Transactional
    public void updatePrice(Long id, Integer minPrice) {
        Lot lot = lotRepository.findByIdOrThrow(id);

        checkLotStatus(lot, ELotStatus.INITIATED);

        lot.setMinPrice(minPrice);

        lotRepository.save(lot);
    }

    @Transactional
    public void delete(Long id) {
        Lot lot = lotRepository.findByIdOrThrow(id);

        checkLotStatus(lot, ELotStatus.INITIATED);
        lotRepository.delete(lot);
    }

    @Transactional
    public void deactivate(Long id) {
        Lot lot = lotRepository.findByIdOrThrow(id);

        checkLotStatus(lot, ELotStatus.ACTIVE);

        List<Application> applications = lot.getApplications();

        for (var application: applications) {
            application.setStatus(EApplicationStatus.INACTIVE);
            applicationRepository.save(application);
        }

        lot.setStatus(ELotStatus.INACTIVE);
        lotRepository.save(lot);
    }

    @Transactional
    public LotFullDto get(Long id) {
        return entityConverterService.toLotFullDto(lotRepository.findByIdOrThrow(id));
    }

    @Transactional
    public GeneralPageableResponse<LotFullDto> getList(HttpServletRequest request, Integer limit, Integer offset) {
        String username = request.getUserPrincipal().getName();
        List<LotFullDto> totalResults = lotRepository.findByCreator_UsernameOrderByIdDesc(username).stream().map(entityConverterService::toLotFullDto).toList();
        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }

    @Scheduled(cron = "0 0 * * *")
    private void handleFinishedLots() {
        Timestamp searchTime = new Timestamp(System.currentTimeMillis() - 14 * DAY_MILLIS - 100_000L);
        List<Lot> lotsToHandle = lotRepository.findFinishedByAuctionEndTimeLessThan(searchTime);

        for (var lot : lotsToHandle) {
            lot.setStatus(ELotStatus.UNSUCCESSFUL);
            lotRepository.save(lot);
        }
    }
}
