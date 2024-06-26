package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.PayUnit;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayUnitRepository extends JpaRepository<PayUnit, Long> {
    default PayUnit findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("No pay unit found"));
    }
}
