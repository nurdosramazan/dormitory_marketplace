package nu.senior_project.dormitory_marketplace.dto.job;

import lombok.Data;

@Data
public class JobRequest {
    private Long id;
    private String name;
    private String description;
    private Long payPerUnit;
    private Long payUnitId;
    private String contactInfo;
    private String qualifications;
}
