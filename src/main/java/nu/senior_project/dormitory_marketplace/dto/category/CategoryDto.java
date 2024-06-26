package nu.senior_project.dormitory_marketplace.dto.category;

import lombok.Builder;
import lombok.Data;
import nu.senior_project.dormitory_marketplace.dto.image.ImageDto;

@Data
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private ImageDto imageDto;
}
