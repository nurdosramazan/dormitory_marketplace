package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Job;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

    default Job findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("no pay unit found"));
    }
}
