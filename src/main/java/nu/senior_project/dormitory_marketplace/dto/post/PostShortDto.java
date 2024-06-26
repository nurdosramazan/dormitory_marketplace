package nu.senior_project.dormitory_marketplace.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.category.CategoryDto;
import nu.senior_project.dormitory_marketplace.dto.image.ImageDto;
import nu.senior_project.dormitory_marketplace.dto.user.UserShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostShortDto {
    private Long id;
    private String name;
    private ImageDto imageDto;
    private Integer price;
    private String description;
    private CategoryDto category;
    private UserShortDto owner;
    private String rating;
}
