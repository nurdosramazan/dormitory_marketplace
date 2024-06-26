package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import nu.senior_project.dormitory_marketplace.enums.ELotStatus;

import java.util.List;

@Entity
@Table(schema = "marketplace", name = "lot")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Min(value=0)
    private Integer minPrice;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Min(value=0)
    private Integer currentPrice;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;


    @OneToMany(mappedBy = "lot", fetch = FetchType.EAGER)
    private List<Application> applications;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ELotStatus status;

}
