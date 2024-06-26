package nu.senior_project.dormitory_marketplace.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsDto {
    private List<PostAnalyticsDto> postsData;
    private Integer totalItemsSold;
    private Integer lastWeekRevenue;
    private Integer lastMonthRevenue;
    private Integer totalEarnings;
}
