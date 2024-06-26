package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.image.PostImage;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    default PostImage findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("PostImage not found"));
    }
    PostImage findFirstByIsMainAndPostId(Boolean isMain, Long postId);
    List<PostImage> findAllByPostIdAndIdNot(Long postId, Long imageId);
    PostImage findTopByPostIdAndIdNotOrderById(Long postId, Long imageId);
}
