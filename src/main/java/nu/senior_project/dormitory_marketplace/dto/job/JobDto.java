package nu.senior_project.dormitory_marketplace.dto.job;

import lombok.Builder;
import lombok.Data;
import nu.senior_project.dormitory_marketplace.dto.user.UserDto;
import java.sql.Timestamp;

@Data
@Builder
public class JobDto {
    private Long id;
    private String name;
    private String description;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private Long payPerUnit;
    private PayUnitDto payUnit;
    private String contactInfo;
    private String qualifications;
    private UserDto owner;
}
