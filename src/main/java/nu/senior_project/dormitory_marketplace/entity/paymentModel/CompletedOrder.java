package nu.senior_project.dormitory_marketplace.entity.paymentModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompletedOrder {
    private String status;
    private String paymentId;
    private String transactionId;

    public CompletedOrder(String status) {
        this.status = status;
    }
}
