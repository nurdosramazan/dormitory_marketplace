package nu.senior_project.dormitory_marketplace.job;

import jakarta.transaction.Transactional;
import nu.senior_project.dormitory_marketplace.entity.Auction;
import nu.senior_project.dormitory_marketplace.entity.Lot;
import nu.senior_project.dormitory_marketplace.enums.EAuctionStatus;
import nu.senior_project.dormitory_marketplace.enums.ELotStatus;
import nu.senior_project.dormitory_marketplace.repository.AuctionRepository;
import nu.senior_project.dormitory_marketplace.repository.LotRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class AuctionInitiationJob extends QuartzJobBean {

    private AuctionRepository auctionRepository;
    private LotRepository lotRepository;

    public void setAuctionRepository(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public void setLotRepository(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        Long auctionId = context.getMergedJobDataMap().getLong("auctionId");
        Auction auction = auctionRepository.findByIdOrThrow(auctionId);

        auction.setStatus(EAuctionStatus.ACTIVE);
        auctionRepository.save(auction);

        List<Lot> lots = auction.getLots();

        for (var lot : lots) {
            lot.setStatus(ELotStatus.ACTIVE);
            lotRepository.save(lot);
        }

    }
}
