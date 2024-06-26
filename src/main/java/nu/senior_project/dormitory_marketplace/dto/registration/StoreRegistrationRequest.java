package nu.senior_project.dormitory_marketplace.dto.registration;

import lombok.Data;

@Data
public class StoreRegistrationRequest extends RegistrationRequest{
    private String storeName;
    private String description;
    private Long categoryId;
    private String paypalEmail;
}
