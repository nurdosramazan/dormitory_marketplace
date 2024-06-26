package nu.senior_project.dormitory_marketplace.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.post.PostShortDto;
import nu.senior_project.dormitory_marketplace.dto.user.UserDto;
import java.sql.Timestamp;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleFullDto {
    private Long id;
    private String status;
    private UserDto buyer;
    private UserDto seller;
    private Integer price;
    private PostShortDto post;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
}
