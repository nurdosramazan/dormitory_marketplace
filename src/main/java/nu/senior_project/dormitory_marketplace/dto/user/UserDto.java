package nu.senior_project.dormitory_marketplace.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstname;
    private String secondName;
    private Boolean isStore;
}
