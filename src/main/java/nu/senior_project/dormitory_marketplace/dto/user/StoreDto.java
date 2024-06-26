package nu.senior_project.dormitory_marketplace.dto.user;

import lombok.Builder;
import lombok.Data;
import nu.senior_project.dormitory_marketplace.dto.image.ImageDto;

import java.util.List;

@Data
@Builder
public class StoreDto {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private List<ImageDto> images;
}
