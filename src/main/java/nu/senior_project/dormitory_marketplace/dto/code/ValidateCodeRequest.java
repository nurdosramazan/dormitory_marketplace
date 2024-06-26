package nu.senior_project.dormitory_marketplace.dto.code;

import lombok.Data;

@Data
public class ValidateCodeRequest {
    private String recipient;
    private String token;
    private String userCode;
}
