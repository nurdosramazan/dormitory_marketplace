package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Auction;
import nu.senior_project.dormitory_marketplace.enums.EAuctionStatus;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    default Auction findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Auction not found"));
    }

    List<Auction> findByStatusIn(List<EAuctionStatus> statuses);

    List<Auction> findAllByOrderByIdDesc();
}
