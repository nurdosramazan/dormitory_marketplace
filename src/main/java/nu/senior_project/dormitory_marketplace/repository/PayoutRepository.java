package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.paymentModel.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
}
