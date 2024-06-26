package nu.senior_project.dormitory_marketplace.dto.user;

import lombok.Data;
import nu.senior_project.dormitory_marketplace.enums.ERole;

import java.util.List;


@Data
public class UserRole {
    private Long id;
    private List<ERole> roles;
}
