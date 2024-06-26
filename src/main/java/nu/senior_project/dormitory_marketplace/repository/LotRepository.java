package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Auction;
import nu.senior_project.dormitory_marketplace.entity.Lot;
import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.enums.ELotStatus;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {
    default Lot findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("no lot found"));
    }
    List<Lot> findByAuctionOrderByIdDesc(Auction auction);

    List<Lot> findByCreator_UsernameOrderByIdDesc(String username);

    Lot findByPostEqualsAndStatusIn(Post post, List<ELotStatus> lotStatuses);

    @Query("SELECT l from Lot l where l.status = 'FINISHED' and l.auction.endTime < :timestamp")
    List<Lot> findFinishedByAuctionEndTimeLessThan(Timestamp timestamp);

    List<Lot> findByPostAndStatusIn(Post post, List<ELotStatus> statuses);
}
