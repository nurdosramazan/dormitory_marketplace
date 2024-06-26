package nu.senior_project.dormitory_marketplace.service;

import co.elastic.clients.elasticsearch.nodes.Http;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.sale.SaleFullDto;
import nu.senior_project.dormitory_marketplace.dto.user.UserInfo;
import nu.senior_project.dormitory_marketplace.dto.user.UserRole;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.repository.SaleRepository;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import nu.senior_project.dormitory_marketplace.entity.image.UserImage;
import nu.senior_project.dormitory_marketplace.repository.UserImageRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final ImageService imageService;
    private final EntityConverterService entityConverterService;
    private final SaleRepository saleRepository;

    public UserInfo getUserInfo(String username) {
        User user = userRepository.findByUsernameOrThrow(username);
        return entityConverterService.toUserInfo(user);
    }

    public UserRole getUserRole(String username) {
        User user = userRepository.findByUsernameOrThrow(username);
        return entityConverterService.toUserRole(user);
    }

    @Transactional
    public void updateUser(Long userId, String newUsername, String newFirstName, String newSecondName) {
        User existingUser = userRepository.findByIdOrThrow(userId);
        if(Strings.isNotBlank(newUsername)) existingUser.setUsername(newUsername);
        if(Strings.isNotBlank(newFirstName)) existingUser.setFirstname(newFirstName);
        if(Strings.isNotBlank(newSecondName)) existingUser.setSecondName(newSecondName);

        userRepository.save(existingUser);
    }

    @Transactional
    public Long updateUserImage(Long userId, MultipartFile image) {
        UserImage existingUserImage = userImageRepository.findByUserId(userId);

        imageService.deleteImageFromFilesystem(existingUserImage.getPath());
        imageService.saveUserImage(image, userId);//check if it updates or adds new

        userImageRepository.save(existingUserImage);

        return userId;
    }

    @Transactional
    public Long deleteUserImage(Long userId) {
        UserImage userImage = userImageRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User image not found for user with ID: " + userId));

        imageService.deleteImageFromFilesystem(userImage.getPath());

        userImageRepository.deleteById(userImage.getId());
        return userId;
    }

    public GeneralPageableResponse<SaleFullDto> getUserPurchases(HttpServletRequest request, Integer limit, Integer offset) {
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsernameOrThrow(username);
        List<SaleFullDto> totalResults = saleRepository.findByBuyerOrderByIdDesc(user)
                .stream()
                .map(entityConverterService::toSaleFullDto)
                .toList();

        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }

    public GeneralPageableResponse<SaleFullDto> getUserSales(HttpServletRequest request, Integer limit, Integer offset) {
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsernameOrThrow(username);
        List<SaleFullDto> totalResults = saleRepository.findBySellerOrderByIdDesc(user)
                .stream()
                .map(entityConverterService::toSaleFullDto)
                .toList();
        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }
}
