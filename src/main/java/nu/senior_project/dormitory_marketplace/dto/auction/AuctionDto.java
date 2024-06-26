package nu.senior_project.dormitory_marketplace.dto.auction;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class AuctionDto {
    private Long id;
    private String name;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp applicationAcceptTime;
}
