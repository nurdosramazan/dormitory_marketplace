package nu.senior_project.dormitory_marketplace.exception.auction;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuctionException extends RuntimeException {
    private String message;
}
