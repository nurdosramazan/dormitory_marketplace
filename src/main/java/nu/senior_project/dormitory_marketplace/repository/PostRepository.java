package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    default Post findByIdOrThrow(Long postId) {
        return findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));
    }
    List<Post> findByOwnerOrderByIdDesc(User user);
}
