package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.Payout;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "marketplace", name = "sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long id;

    @Column(name = "status")
    private Short status;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "price")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "created")
    @CreationTimestamp
    private Timestamp createdTime;

    @Column(name = "modified")
    @UpdateTimestamp
    private Timestamp modifiedTime;

    @Column(name = "transaction_id")
    private String transactionId;

    @OneToMany(mappedBy = "sale")
    private List<Payout> payouts;
}
