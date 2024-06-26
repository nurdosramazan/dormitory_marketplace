package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Store;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByOwnerId(Long ownerId);

    default Store findByOwnerIdOrThrow(Long ownerId) {
        return findByOwnerId(ownerId).orElseThrow(() -> new NotFoundException("Store not found"));
    }
}
