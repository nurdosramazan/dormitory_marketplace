package nu.senior_project.dormitory_marketplace.entity.paymentModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder implements Serializable {
    private String status;
    private String paymentId;
    private String redirectUrl;

    public PaymentOrder(String status) {
        this.status = status;
    }
}