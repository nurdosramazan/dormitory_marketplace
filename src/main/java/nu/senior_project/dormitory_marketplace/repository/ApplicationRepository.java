package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Application;
import nu.senior_project.dormitory_marketplace.entity.Lot;
import nu.senior_project.dormitory_marketplace.enums.EApplicationStatus;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    default Application findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Application not found"));
    }
    Application findByLotAndStatus(Lot lot, EApplicationStatus status);

    List<Application> findByApplicant_UsernameOrderByIdDesc(String username);
}
