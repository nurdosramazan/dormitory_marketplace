package nu.senior_project.dormitory_marketplace.dto.auth;

import lombok.Data;
import nu.senior_project.dormitory_marketplace.dto.GeneralResponseModel;

@Data
public class AuthenticationResult extends GeneralResponseModel {
    private String token;
}
