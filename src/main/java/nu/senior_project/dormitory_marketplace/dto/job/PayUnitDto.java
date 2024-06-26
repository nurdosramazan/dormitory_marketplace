package nu.senior_project.dormitory_marketplace.dto.job;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayUnitDto {
    private Long id;
    private String name;
}
