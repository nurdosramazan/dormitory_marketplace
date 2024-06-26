package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.enums.EAuctionStatus;

import java.sql.Timestamp;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(schema = "marketplace", name = "auction")
@Data
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Timestamp applicationAcceptTime;
    private Timestamp startTime;
    private Timestamp endTime;

    @Enumerated(EnumType.STRING)
    private EAuctionStatus status;

    @OneToMany(mappedBy = "auction", fetch = FetchType.EAGER)
    private List<Lot> lots;
}
