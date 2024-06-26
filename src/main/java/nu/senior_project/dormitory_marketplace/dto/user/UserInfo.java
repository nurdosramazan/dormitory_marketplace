package nu.senior_project.dormitory_marketplace.dto.user;

import lombok.Builder;
import lombok.Data;
import nu.senior_project.dormitory_marketplace.dto.image.ImageDto;
import nu.senior_project.dormitory_marketplace.enums.ERole;

import java.util.List;

@Data
@Builder
public class UserInfo {
    private Long id;
    private List<ERole> roles;
    private String username;
    private String firstname;
    private String secondName;
    private String email;
    private ImageDto profileImage;
    private StoreDto storeInfo;
}
