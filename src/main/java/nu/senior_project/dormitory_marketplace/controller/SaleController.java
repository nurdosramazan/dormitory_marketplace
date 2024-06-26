package nu.senior_project.dormitory_marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nu.senior_project.dormitory_marketplace.dto.sale.SaleFullDto;
import nu.senior_project.dormitory_marketplace.dto.sale.SaleRequest;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.CompletedOrder;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.PaymentOrder;
import nu.senior_project.dormitory_marketplace.service.RatingService;
import nu.senior_project.dormitory_marketplace.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sale")
@CrossOrigin
@Slf4j
public class SaleController {
    @Autowired
    private SaleService saleService;
    @Autowired
    private RatingService ratingService;
    @PostMapping("/create/{postId}")
    public ResponseEntity<?> createSale(HttpServletRequest request,
                                                  @PathVariable Long postId) {
        try {
            SaleFullDto result = saleService.create(request, postId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating sale");
        }
    }

    @PutMapping("/{saleId}/approveBySeller")
    @PreAuthorize("@permissionCheck.checkSaleProcessingRights(authentication, #saleId, 'seller')")
    public ResponseEntity<String> approveBySeller(@PathVariable Long saleId) {
        try {
            saleService.approveBySeller(saleId);
            return ResponseEntity.ok("Sale " + saleId + " is approved by seller!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving sale by the seller");
        }
    }

    @PutMapping("/{saleId}/approveByBuyer")
    @PreAuthorize("@permissionCheck.checkSaleProcessingRights(authentication, #saleId, 'buyer')")
    public ResponseEntity<String> approveByBuyer(@PathVariable Long saleId) {
        try {
            saleService.approveByBuyer(saleId);
            return ResponseEntity.ok("Sale " + saleId + " is approved by buyer!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving sale by the buyer");
        }
    }

    @PutMapping("/{saleId}/cancelBySeller")
    @PreAuthorize("@permissionCheck.checkSaleProcessingRights(authentication, #saleId, 'seller')")
    public ResponseEntity<String> cancelBySeller(@PathVariable Long saleId) {
        try {
            saleService.cancelBySeller(saleId);
            return ResponseEntity.ok("Sale " + saleId + " was cancelled by the seller");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @PutMapping("/{saleId}/cancelByBuyer")
    @PreAuthorize("@permissionCheck.checkSaleProcessingRights(authentication, #saleId, 'buyer')")
    public ResponseEntity<String> cancelByBuyer(@PathVariable Long saleId) {
        try {
            String refundResponse = saleService.cancelByBuyer(saleId);
            return ResponseEntity.ok("Sale " + saleId + " was cancelled by the buyer: " + refundResponse);
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
    @PostMapping("/{saleId}/initiatePayment")
    @PreAuthorize("@permissionCheck.checkSaleProcessingRights(authentication, #saleId, 'buyer')")
    public ResponseEntity<PaymentOrder> initiatePayment(@PathVariable Long saleId) {
        try {
            return ResponseEntity.ok(saleService.initiatePayment(saleId));
        } catch (Exception e) {
            log.error("Exception for sale " + saleId + " in initiating payment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{saleId}/completePayment")
    public ResponseEntity<CompletedOrder> completePayment(@PathVariable Long saleId, @RequestParam String token) {
        try {
            return ResponseEntity.ok(saleService.completePayment(saleId, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{saleId}/payToSeller")
    public ResponseEntity<String> payToSeller(@PathVariable Long saleId) {
        try {
            String payResponse = saleService.payToSeller(saleId);
            return ResponseEntity.ok("The money has been sent to the seller: " + payResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending money to the seller");
        }
    }

    @PostMapping("/{saleId}/rate")
    @PreAuthorize("@permissionCheck.checkSaleProcessingRights(authentication, #saleId, 'buyer')")
    public ResponseEntity<String> rate(@PathVariable Long saleId, Integer stars, String comment) {
        try {
            ratingService.rate(saleId, stars, comment);
            return ResponseEntity.ok("Good with sale id " + saleId + " has been rated as " + stars);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error rating");
        }
    }
}
