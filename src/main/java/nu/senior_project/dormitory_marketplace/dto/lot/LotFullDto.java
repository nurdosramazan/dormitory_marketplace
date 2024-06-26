package nu.senior_project.dormitory_marketplace.dto.lot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import nu.senior_project.dormitory_marketplace.dto.auction.AuctionDto;
import nu.senior_project.dormitory_marketplace.dto.post.PostDto;
import nu.senior_project.dormitory_marketplace.dto.user.UserDto;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LotFullDto extends LotDto {
    private AuctionDto auction;
    private UserDto creator;
    private PostDto post;
    private Integer numberOfBids;
}
