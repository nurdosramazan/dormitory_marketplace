package nu.senior_project.dormitory_marketplace.dto.code;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SendCodeRequest {
    @Email(regexp = "^[\\w.+\\-]*@nu.edu.kz$", message = "invalid email format: has to be a valid NU email address")
    private String recipient;
}
