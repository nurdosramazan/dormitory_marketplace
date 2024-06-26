package nu.senior_project.dormitory_marketplace.job;

import nu.senior_project.dormitory_marketplace.entity.Auction;
import nu.senior_project.dormitory_marketplace.entity.Lot;
import nu.senior_project.dormitory_marketplace.enums.EApplicationStatus;
import nu.senior_project.dormitory_marketplace.enums.EAuctionStatus;
import nu.senior_project.dormitory_marketplace.enums.ELotStatus;
import nu.senior_project.dormitory_marketplace.enums.ESaleStatus;
import nu.senior_project.dormitory_marketplace.repository.ApplicationRepository;
import nu.senior_project.dormitory_marketplace.repository.AuctionRepository;
import nu.senior_project.dormitory_marketplace.repository.LotRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class AuctionFinishJob extends QuartzJobBean {
    private AuctionRepository auctionRepository;
    private LotRepository lotRepository;

    private ApplicationRepository applicationRepository;

    public void setAuctionRepository(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public void setLotRepository(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        Long auctionId = context.getMergedJobDataMap().getLong("auctionId");
        Auction auction = auctionRepository.findByIdOrThrow(auctionId);

        auction.setStatus(EAuctionStatus.FINISHED);

        List<Lot> lots = auction.getLots();

        for (var lot : lots) {
            lot.setStatus(ELotStatus.FINISHED);
            lotRepository.save(lot);

            for (var application : lot.getApplications()) {
                if (application.getStatus().equals(EApplicationStatus.WINNING)) {
                    application.setStatus(EApplicationStatus.WINNER);
                    applicationRepository.save(application);
                }
            }
        }

        auctionRepository.save(auction);

    }
}
