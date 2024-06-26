package nu.senior_project.dormitory_marketplace.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nu.senior_project.dormitory_marketplace.entity.Category;
import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.entity.image.CategoryImage;
import nu.senior_project.dormitory_marketplace.entity.image.PostImage;
import nu.senior_project.dormitory_marketplace.entity.image.UserImage;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import nu.senior_project.dormitory_marketplace.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final PostImageRepository postImageRepository;

    private final CategoryImageRepository categoryImageRepository;

    private final UserImageRepository userImageRepository;

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;


    @Value("${imagesRoot}")
    private String imagesRoot;

    private final String POST = "post";
    private final String CATEGORY = "category";
    private final String USER = "user";
    @Transactional
    public void savePostImages(List<MultipartFile> images, Long postId) {
        if (images == null)
            return;

        Post post = postRepository.findByIdOrThrow(postId);

        Path destination = getDestinationPath(POST, postId);
        for (var image : images) {
            String absolutePath = saveImageToFilesystem(image, destination);
            postImageRepository.save(PostImage.builder()
                    .post(post)
                    .path(absolutePath)
                    .isMain(Boolean.TRUE)
                    .build());
        }
    }

    @Transactional
    public void updatePostImage(Long imageId, MultipartFile image) {
        PostImage existingImage = postImageRepository.findByIdOrThrow(imageId);

        deleteImageFromFilesystem(existingImage.getPath());

        Path destination = getDestinationPath(POST, existingImage.getPost().getId());
        String absolutePath = saveImageToFilesystem(image, destination);

        existingImage.setPath(absolutePath);

        postImageRepository.save(existingImage);
    }

    @Transactional
    public void saveCategoryImage(MultipartFile image, Long categoryId) {
        Category category = categoryRepository.findByIdOrThrow(categoryId);

        Path destination = getDestinationPath(CATEGORY, categoryId);

        String absolutePath = saveImageToFilesystem(image, destination);
        categoryImageRepository.save(CategoryImage.builder()
                .category(category)
                .path(absolutePath)
                .build());
    }

    @Transactional
    public void updateCategoryImage(MultipartFile image, Long categoryId) {

        CategoryImage existingImage = categoryImageRepository.findCategoryImageByCategoryId(categoryId);

        if (existingImage == null)
            throw new NotFoundException("No CategoryImage found");

        deleteImageFromFilesystem(existingImage.getPath());

        Path destination = getDestinationPath(CATEGORY, categoryId);
        String absolutePath = saveImageToFilesystem(image, destination);

        existingImage.setPath(absolutePath);

        categoryImageRepository.save(existingImage);

    }

    @SneakyThrows
    private Path getDestinationPath(String directory, Long id) {
        StringJoiner path = new StringJoiner("/", imagesRoot + "/", "");
        Path destinationDirectory = Paths.get(path.add(directory).add(id.toString()).toString());

        Files.createDirectories(destinationDirectory);

        return destinationDirectory;
    }

    @SneakyThrows
    private String saveImageToFilesystem(MultipartFile file, Path destinationDirectory) {
        Path destinationFilename = destinationDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFilename);
        } catch (IOException e) {
            throw new RuntimeException("Error when saving file");
        }

        return destinationFilename.toString();

    }

    public void saveUserImage(MultipartFile image, Long userId) {
        User user = userRepository.findByIdOrThrow(userId);
        Path destination = getDestinationPath(USER, userId);
        String absolutePath = saveImageToFilesystem(image, destination);

        userImageRepository.save(UserImage.builder()
                .path(absolutePath)
                .user(user)
                .build());
    }
    @SneakyThrows
    public void deleteImageFromFilesystem(String imagePath) {
        Path existingImage = Paths.get(imagePath);
        Files.deleteIfExists(existingImage);
    }

}
