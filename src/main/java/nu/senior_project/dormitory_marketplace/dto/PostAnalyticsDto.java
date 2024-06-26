package nu.senior_project.dormitory_marketplace.dto;

import lombok.Data;
import nu.senior_project.dormitory_marketplace.dto.post.PostShortDto;

@Data
public class PostAnalyticsDto {
    private PostShortDto postShortDto;
    private Integer soldCount;
    private Integer totalEarnings;
}
