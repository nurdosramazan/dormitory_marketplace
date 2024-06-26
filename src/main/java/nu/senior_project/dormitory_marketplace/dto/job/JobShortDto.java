package nu.senior_project.dormitory_marketplace.dto.job;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobShortDto {
    private Long id;
    private String name;
    private String description;
    private Long payPerUnit;
    private PayUnitDto payUnit;
}
