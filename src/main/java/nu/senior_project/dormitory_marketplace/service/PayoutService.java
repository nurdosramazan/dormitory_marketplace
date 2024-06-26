package nu.senior_project.dormitory_marketplace.service;

import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.Sale;
import nu.senior_project.dormitory_marketplace.entity.Store;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.Payout;
import nu.senior_project.dormitory_marketplace.repository.PayoutRepository;
import nu.senior_project.dormitory_marketplace.repository.StoreRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayoutService {
    private final PayoutRepository payoutRepository;
    private final StoreRepository storeRepository;

    public Payout createPayout(Sale sale) {
        Store seller = storeRepository.findByOwnerIdOrThrow(sale.getSeller().getId());

        Payout payout = new Payout();
        payout.setEmail(seller.getPaypalEmail());
        payout.setAmount(sale.getPrice());
        payout.setTransactionId(sale.getTransactionId());
        payout.setSale(sale);
        payout.setSeller(sale.getSeller());

        payoutRepository.save(payout);
        return payout;
    }
}
