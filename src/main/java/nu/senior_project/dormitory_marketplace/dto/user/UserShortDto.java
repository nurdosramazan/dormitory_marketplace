package nu.senior_project.dormitory_marketplace.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDto {
    private String username;
    private String firstName;
    private String secondName;
    private Long id;
}
