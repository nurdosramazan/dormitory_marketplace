package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Role;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    default Role findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Role not found"));
    }
}
