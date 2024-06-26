package nu.senior_project.dormitory_marketplace.dto.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ApplicationDto {
    protected Integer price;
    protected Long lotId;
}
