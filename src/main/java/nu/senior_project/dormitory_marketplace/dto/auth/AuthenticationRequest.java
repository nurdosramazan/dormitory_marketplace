package nu.senior_project.dormitory_marketplace.dto.auth;

import lombok.Data;

@Data
public class AuthenticationRequest {
    String username;
    String password;
}
