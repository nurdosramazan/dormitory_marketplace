package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    default User findByUsernameOrThrow(String username) {
         return findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

}
