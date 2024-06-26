package nu.senior_project.dormitory_marketplace.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.image.ImageDto;
import nu.senior_project.dormitory_marketplace.dto.user.UserShortDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String description;
    private Integer price;
    private UserShortDto owner;
    private List<ImageDto> images = new ArrayList<>();
}
