package nu.senior_project.dormitory_marketplace.dto.application;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import nu.senior_project.dormitory_marketplace.dto.lot.LotFullDto;
import nu.senior_project.dormitory_marketplace.dto.user.UserDto;
import nu.senior_project.dormitory_marketplace.enums.EApplicationStatus;

@Data
@SuperBuilder
public class ApplicationFullDto extends ApplicationDto {
    private UserDto applicant;
    private LotFullDto lot;
    private Long id;
    private EApplicationStatus status;
}
