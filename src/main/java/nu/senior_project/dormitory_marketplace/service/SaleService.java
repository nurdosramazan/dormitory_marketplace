package nu.senior_project.dormitory_marketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.sale.SaleFullDto;
import nu.senior_project.dormitory_marketplace.dto.sale.SaleRequest;
import nu.senior_project.dormitory_marketplace.entity.*;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.CompletedOrder;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.PaymentOrder;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.Payout;
import nu.senior_project.dormitory_marketplace.enums.EApplicationStatus;
import nu.senior_project.dormitory_marketplace.enums.ELotStatus;
import nu.senior_project.dormitory_marketplace.enums.EProductType;
import nu.senior_project.dormitory_marketplace.enums.ESaleStatus;
import nu.senior_project.dormitory_marketplace.exception.InsufficientRightsException;
import nu.senior_project.dormitory_marketplace.exception.auction.AuctionException;
import nu.senior_project.dormitory_marketplace.repository.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LotRepository lotRepository;
    private final EntityConverterService entityConverterService;
    private final PaypalService paypalService;
    private final StoreRepository storeRepository;
    private final PayoutService payoutService;
    private final PayoutRepository payoutRepository;

    public SaleFullDto create(HttpServletRequest request, Long postId) {
        Post post = postRepository.findByIdOrThrow(postId);

        String username = request.getUserPrincipal().getName();
        User buyer = userRepository.findByUsernameOrThrow(username);
        User seller = post.getOwner();

        checkAuctionConditionsForInitiation(post, buyer);

        Sale sale = entityConverterService.toSale(post, buyer, seller);
        saleRepository.save(sale);
        return entityConverterService.toSaleFullDto(sale);
    }

    private void checkAuctionConditionsForInitiation(Post post, User buyer) {
        if (post.getProductType().equals(EProductType.SINGLE)) {
            Lot lot = lotRepository.findByPostEqualsAndStatusIn(post, Arrays.asList(
                    ELotStatus.FINISHED, ELotStatus.ACTIVE, ELotStatus.INITIATED, ELotStatus.SALE
                    )
            );

            if (lot != null) {
                if (!lot.getStatus().equals(ELotStatus.FINISHED))
                    throw new AuctionException("Product is under auction conditions. Purchase is not available");

                List<Application> applications = lot.getApplications()
                        .stream()
                        .filter(application -> application.getStatus().equals(EApplicationStatus.WINNER))
                        .toList();

                if (applications.size() != 1)
                    throw new IllegalStateException("no winner for the finished lot.");

                if (!applications.get(0).getApplicant().equals(buyer))
                    throw new AuctionException("You cannot purchase, product is reserved by a lot winner");

                lot.setStatus(ELotStatus.SALE);
                lotRepository.save(lot);
            }

        }
    }

    public void approveBySeller(Long saleId) {
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        checkStatus(sale, ESaleStatus.NEEDS_SELLER_VALIDATION.getValue());
        sale.setStatus(ESaleStatus.NEEDS_BUYER_VALIDATION.getValue());

        saleRepository.save(sale);
    }

    public void approveByBuyer(Long saleId) {
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        checkStatus(sale, ESaleStatus.PAID.getValue());
        checkAuctionConditionsForFinishing(sale.getPost());

        sale.setStatus(ESaleStatus.APPROVED_BY_BUYER.getValue());
        saleRepository.save(sale);
    }

    private void checkAuctionConditionsForFinishing(Post post) {
        if (post.getProductType().equals(EProductType.SINGLE)) {
            Lot lot = lotRepository.findByPostEqualsAndStatusIn(post, List.of(ELotStatus.SALE));
            if (lot != null) {
                lot.setStatus(ELotStatus.SUCCESSFUL);
                lotRepository.save(lot);
            }
        }
    }

    private void checkAuctionConditionsForCanceling(Post post) {
        if (post.getProductType().equals(EProductType.SINGLE)) {
            Lot lot = lotRepository.findByPostEqualsAndStatusIn(post, List.of(ELotStatus.SALE));
            if (lot != null) {
                lot.setStatus(ELotStatus.UNSUCCESSFUL);
                lotRepository.save(lot);
            }
        }
    }

    public String cancelByBuyer(Long saleId) {
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        checkAuctionConditionsForCanceling(sale.getPost());
        if (sale.getStatus() > ESaleStatus.PAID.getValue()) {
            throw new IllegalStateException("Sorry, you cannot cancel the transaction at this stage!");
        } else {
            String refundResponse = paypalService.refundPayment(sale.getTransactionId());
            sale.setStatus(ESaleStatus.CANCELLED_BY_BUYER.getValue());

            saleRepository.save(sale);
            return refundResponse;
        }
    }

    public void cancelBySeller(Long saleId) {
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        checkAuctionConditionsForCanceling(sale.getPost());
        if (sale.getStatus() >= ESaleStatus.PAID.getValue()) {
            throw new IllegalStateException("Sorry, you cannot cancel the transaction at this stage!");
        } else {
            sale.setStatus(ESaleStatus.CANCELLED_BY_SELLER.getValue());
            saleRepository.save(sale);
        }
    }

    private void checkStatus(Sale sale, Short neededStatus) {
        if (!sale.getStatus().equals(neededStatus))
            throw new InsufficientRightsException("No rights to change current status");
    }

    public PaymentOrder initiatePayment(Long saleId) {
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        checkStatus(sale, ESaleStatus.NEEDS_BUYER_VALIDATION.getValue());

        return paypalService.createPayment(sale.getId(), sale.getPrice());
    }

    public CompletedOrder completePayment(Long saleId, String token) {
        CompletedOrder completedOrder = paypalService.completePayment(token);

        Sale sale = saleRepository.findByIdOrThrow(saleId);
        sale.setTransactionId(completedOrder.getTransactionId());
        sale.setStatus(ESaleStatus.PAID.getValue());
        saleRepository.save(sale);

        return completedOrder;
    }

    public String payToSeller(Long saleId) {
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        Store seller = storeRepository.findByOwnerIdOrThrow(sale.getSeller().getId());
        checkStatus(sale, ESaleStatus.APPROVED_BY_BUYER.getValue());

        Payout payout = payoutService.createPayout(sale);
        String payResponse = paypalService.payTo(payout.getId(), seller.getPaypalEmail(), sale.getPrice());

        sale.setStatus(ESaleStatus.FINISHED.getValue());
        payout.setSuccess(true);
        saleRepository.save(sale);
        payoutRepository.save(payout);

        return payResponse;
    }
}
