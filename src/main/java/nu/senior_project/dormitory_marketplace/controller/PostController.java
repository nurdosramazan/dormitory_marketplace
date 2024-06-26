package nu.senior_project.dormitory_marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.post.PostDto;
import nu.senior_project.dormitory_marketplace.dto.post.PostShortDto;
import nu.senior_project.dormitory_marketplace.entity.Rating;
import nu.senior_project.dormitory_marketplace.service.ImageService;
import nu.senior_project.dormitory_marketplace.service.PostService;
import nu.senior_project.dormitory_marketplace.service.RatingService;
import nu.senior_project.dormitory_marketplace.service.elastic.PostDocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@CrossOrigin
public class PostController {

    private final PostService postService;

    private final ImageService imageService;

    private final PostDocumentService postDocumentService;

    private final RatingService ratingService;

    @PostMapping("/create")
    public Long createPost(@RequestParam() String title,
                            @RequestParam() String description,
                            @RequestParam() Long categoryId,
                            @RequestParam() Integer price,
                            @RequestParam(required = false) List<MultipartFile> images,
                            HttpServletRequest httpRequest) {
        Long postId = postService.createPost(title, description, price, categoryId, httpRequest);
        imageService.savePostImages(images, postId);
        return postId;
    }

    @GetMapping("/list")
    public List<PostShortDto> getPosts(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) Long categoryId,
                                        @RequestParam(required = false) Long ownerId,
                                        @RequestParam(required = false) Integer priceMin,
                                        @RequestParam(required = false) Integer priceMax,
                                        @RequestParam(required = false) Integer limit,
                                        @RequestParam(required = false) Integer offset) {
        return postService.getPosts(title, categoryId, ownerId, priceMin, priceMax, limit, offset);
    }

    @GetMapping("/by-user/{id}")
    public GeneralPageableResponse<PostShortDto> getUserPosts(@PathVariable Long id,
                                                              @RequestParam Integer limit,
                                                              @RequestParam Integer offset) {
        return postService.getUserPosts(id, limit, offset);
    }

    @GetMapping("/search")
    public GeneralPageableResponse<PostShortDto> searchPosts(@RequestParam(required = false) String queryText,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) Integer priceMin,
                                           @RequestParam(required = false) Integer priceMax,
                                           @RequestParam(required = false) Integer limit,
                                           @RequestParam(required = false) Integer offset) {
        return postDocumentService.getPosts(queryText, categoryId, priceMin, priceMax, limit, offset);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@permissionCheck.checkPostOwnership(authentication, #id)")
    public Long updatePost(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String description,
                            @RequestParam Long categoryId,
                            @RequestParam Integer price) {
        return postService.updatePost(id, title, description, price, categoryId);
    }

    @PutMapping("/update-image")
    @PreAuthorize("@permissionCheck.checkPostOwnership(authentication, #id)")
    public Long updatePostImage(@RequestParam Long id,
                            @RequestParam() MultipartFile image) {
        return postService.updatePostImage(id, image);
    }

    @PutMapping("/add-image")
    @PreAuthorize("@permissionCheck.checkPostOwnership(authentication, #id)")
    public Long addPostImage(@RequestParam Long id,
                              @RequestParam List<MultipartFile> images) {
        imageService.savePostImages(images, id);
        return id;
    }

    @PutMapping("/setMainImage/{postId}/{imageId}")
    @PreAuthorize("@permissionCheck.checkPostOwnership(authentication, #postId)")
    public Long setMainImage(@PathVariable Long postId, @PathVariable Long imageId) {
        return postService.setMainImage(postId, imageId);
    }

    @DeleteMapping("/{postId}/deleteImage/{imageId}")
    @PreAuthorize("@permissionCheck.checkPostOwnership(authentication, #postId)")
    public Long deleteImage(@PathVariable Long postId, @PathVariable Long imageId) {
        return postService.deleteImage(postId, imageId);
    }

    @GetMapping("/{postId}/getRatings")
    public List<Rating> getRatings(@PathVariable Long postId) {
        return ratingService.getRatings(postId);
    }

    @GetMapping("/{postId}/getAverageRating")
    public Double getAverageRating(@PathVariable Long postId) {
        return postService.getAverageRating(postId);
    }
}
