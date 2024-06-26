package nu.senior_project.dormitory_marketplace.dto.lot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LotDto {
    protected Long id;
    protected Long postId;
    protected Long auctionId;
    protected Integer minPrice;
    protected Integer currentPrice;
}
