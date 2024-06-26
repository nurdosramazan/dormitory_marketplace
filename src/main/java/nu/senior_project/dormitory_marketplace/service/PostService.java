package nu.senior_project.dormitory_marketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.post.PostDto;
import nu.senior_project.dormitory_marketplace.dto.post.PostRequest;
import nu.senior_project.dormitory_marketplace.dto.post.PostShortDto;
import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.entity.elastic.PostDocument;
import nu.senior_project.dormitory_marketplace.entity.image.PostImage;
import nu.senior_project.dormitory_marketplace.repository.PostImageRepository;
import nu.senior_project.dormitory_marketplace.repository.PostRepository;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import nu.senior_project.dormitory_marketplace.service.elastic.PostDocumentService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final EntityConverterService entityConverterService;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostImageRepository postImageRepository;

    private final PostImageRepository imageRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final CategoryService categoryService;

    private final ImageService imageService;

    private final PostDocumentService postDocumentService;


    @Transactional
    public Long createPost(String title,
                           String description,
                           Integer price,
                           Long categoryId,
                           HttpServletRequest request) { // need to add the image as well and apply setMainImage

        String username = request.getUserPrincipal().getName();
        Post post = entityConverterService.toPost(PostRequest.builder()
                .title(title)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .build(), username);
        postRepository.save(post);
        postDocumentService.save(new PostDocument(post));
        return post.getId();
    }

    @Transactional
    public List<PostShortDto> getPosts(String title,
                                       Long categoryId,
                                       Long ownerId,
                                       Integer priceMin,
                                       Integer priceMax,
                                       Integer limit,
                                       Integer offset) {

        List<PostShortDto> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        String select = "select post_id, title, price, description from marketplace.post where 1 = 1";
        String orderBy = "order by title";

        StringJoiner query = new StringJoiner(" ");
        query.add(select);

        if (title != null) {
            params.put("title", title + "%");
            query.add("and lower(title) like :title");
        }

        if (categoryId != null) {
            params.put("categoryId", categoryId);
            query.add("and category_id = :categoryId");
        }

        if (ownerId != null) {
            params.put("ownerId", ownerId);
            query.add("and owner_id = :ownerId");
        }

        if (priceMin != null) {
            params.put("priceMin", priceMin);
            query.add("and price >= :priceMin");
        }

        if (priceMax != null) {
            params.put("priceMax", priceMax);
            query.add("and price <= :priceMax");
        }

        query.add(orderBy);

        if (limit != null) {
            params.put("limit", limit);
            query.add("limit :limit");
        }

        if (offset != null) {
            params.put("offset", offset);
            query.add("offset :offset");
        }


        jdbcTemplate.query(query.toString(), params, resultSet -> {
            while (resultSet.next()) {
                PostShortDto postShortDto =  PostShortDto.builder()
                                                .id(resultSet.getLong("post_id"))
                                                .price(resultSet.getInt("price"))
                                                .name(resultSet.getString("title"))
                                                .description(resultSet.getString("description"))
                                                .build();
                PostImage mainImage = imageRepository.findFirstByIsMainAndPostId(Boolean.TRUE, resultSet.getLong("post_id"));
                postShortDto.setImageDto(entityConverterService.toImageDto(mainImage));
                result.add(postShortDto);
            }
            return null;
        });
        return result;
    }

    @Transactional
    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(RuntimeException::new);
        return entityConverterService.toPostDto(post);
    }
    @Transactional
    public Long updatePost(Long id,
                           String title,
                           String description,
                           Integer price,
                           Long categoryId) {

        Post post = postRepository.findById(id).orElseThrow(RuntimeException::new);

        if(Strings.isNotBlank(title)) post.setTitle(title);
        if(Strings.isNotBlank(description))post.setDescription(description);
        if(Objects.nonNull(price))post.setPrice(price);
        if(Objects.nonNull(categoryId)) post.setCategory(categoryService.getCategoryEntity(categoryId));

        postRepository.save(post);
        postDocumentService.save(new PostDocument(post));
        return post.getId();
    }
    @Transactional
    public Long updatePostImage(Long imageId,
                                MultipartFile images) {
        imageService.updatePostImage(imageId, images);
        return imageId;
    }

    @Transactional
    public Long setMainImage(Long postId, Long imageId) {
        PostImage imageToSetAsMain = postImageRepository.findByIdOrThrow(imageId);

        imageToSetAsMain.setIsMain(true);
        postImageRepository.save(imageToSetAsMain);

        postImageRepository.findAllByPostIdAndIdNot(postId, imageId)
                .forEach(image -> {
                    image.setIsMain(false);
                    postImageRepository.save(image);
                });
        return imageToSetAsMain.getId();
    }

    @Transactional
    public Long deleteImage(Long postId, Long imageId) {
        PostImage imageToDelete = postImageRepository.findByIdOrThrow(imageId);
        if (imageToDelete.getIsMain()) {
            PostImage nextMainImage = postImageRepository.findTopByPostIdAndIdNotOrderById(postId, imageId);
            if (nextMainImage != null) {
                setMainImage(postId, nextMainImage.getId());
            }
        }
        imageService.deleteImageFromFilesystem(imageToDelete.getPath());
        postImageRepository.deleteById(imageId);
        return postId;
    }

    public GeneralPageableResponse<PostShortDto> getUserPosts(Long id, Integer limit, Integer offset) {

        User user = userRepository.findByIdOrThrow(id);
        List<PostShortDto> totalResults = postRepository.findByOwnerOrderByIdDesc(user).stream().map(entityConverterService::toPostShortDto).toList();

        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }

    public Double getAverageRating(Long postId) {
        return postRepository.findByIdOrThrow(postId).getAverageRating();
    }
}
