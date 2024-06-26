package nu.senior_project.dormitory_marketplace.dto.post;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class PostRequest {
    @Length(min = 2, max = 40, message = "title should be in range of (2-40) characters")
    private String title;
    @Length(min = 10, message = "password should be at least 10 characters")
    private String description;
    private Integer price;
    private Long categoryId;
}
