package nu.senior_project.dormitory_marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.sale.SaleFullDto;
import nu.senior_project.dormitory_marketplace.dto.user.UserInfo;
import nu.senior_project.dormitory_marketplace.dto.user.UserRole;
import nu.senior_project.dormitory_marketplace.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController()
@RequestMapping("/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @GetMapping("/info")
    public UserInfo getUserInfo(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        return userService.getUserInfo(username);
    }

    @GetMapping("/role")
    public UserRole getUserRole(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        return userService.getUserRole(username);
    }

    @PutMapping("/update/{userId}") //works
    @PreAuthorize("@permissionCheck.checkUserInfoUpdateRights(authentication, #userId)")
    public Long updateUser(@PathVariable Long userId,
                            @RequestParam(required = false) String newUsername,
                            @RequestParam(required = false) String newFirstName,
                            @RequestParam(required = false) String newSecondName) {
        userService.updateUser(userId, newUsername, newFirstName, newSecondName);
        return userId;
    }

    @PutMapping("/update-image/{userId}")
    @PreAuthorize("@permissionCheck.checkUserInfoUpdateRights(authentication, #userId)")
    public Long updateUserImage(@PathVariable Long userId,
                                 @RequestParam() MultipartFile image) {
        return userService.updateUserImage(userId, image);
    }

    @DeleteMapping("/delete-image/{userId}")
    @PreAuthorize("@permissionCheck.checkUserInfoUpdateRights(authentication, #userId)")
    public Long deleteUserImage(@PathVariable Long userId) {
        return userService.deleteUserImage(userId);
    }


    @GetMapping("/purchases")
    public GeneralPageableResponse<SaleFullDto> getUserPurchases(@RequestParam Integer limit,
                                                                 @RequestParam Integer offset,
                                                                 HttpServletRequest request) {
        return userService.getUserPurchases(request, limit, offset);
    }
    @GetMapping("/sales")
    public GeneralPageableResponse<SaleFullDto> getUserSales(@RequestParam Integer limit,
                                                             @RequestParam Integer offset,
                                                             HttpServletRequest request) {
        return userService.getUserSales(request, limit, offset);
    }
}