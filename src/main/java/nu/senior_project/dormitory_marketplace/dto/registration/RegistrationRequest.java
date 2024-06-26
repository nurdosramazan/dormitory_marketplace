package nu.senior_project.dormitory_marketplace.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegistrationRequest {
    @Email(regexp = "^[\\w.+\\-]*@nu.edu.kz$", message = "Invalid email address format: either not an email address or non-NU email address")
    private String email;
    @Length(min = 8, message = "password too short")
    private String password;
    @Length(min = 2, message = "firstname too short")
    private String firstname;
    @Length(min = 2, message = "secondname too short")
    private String secondName;
    @Length(min = 10, max = 10, message = "invalid token format")
    private String token;
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    private String username;
}
