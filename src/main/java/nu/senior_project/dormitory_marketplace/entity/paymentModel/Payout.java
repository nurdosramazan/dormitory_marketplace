package nu.senior_project.dormitory_marketplace.entity.paymentModel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.Sale;
import nu.senior_project.dormitory_marketplace.entity.User;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "marketplace", name = "payout")
public class Payout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long id;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "receiver_email")
    private String email;

    @Column(name = "is_successful")
    private Boolean success;

    @Column(name = "for_transaction")
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
}
