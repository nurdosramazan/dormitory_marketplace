package nu.senior_project.dormitory_marketplace.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.CompletedOrder;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.PaymentOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaypalService {
    private final PayPalHttpClient payPalHttpClient;

    @Value("${paypal.refund.url}")
    private String refundUrl;

    @Value("${paypal.refund.user}")
    private String user;

    @Value("${paypal.refund.password}")
    private String password;

    @Value("${paypal.refund.signature}")
    private String signature;

    @Value("${paypal.pay.url}")
    private String payUrl;

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.clientSecret}")
    private String secret;

    @Value("${paypal.redirectUrl.capture}")
    private String redirectUrlCapture;

    @Value("${paypal.redirectUrl.cancel}")
    private String redirectUrlCancel;

    public PaymentOrder createPayment(Long saleId, Integer fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown()
                .currencyCode("USD").value(fee.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(amountWithBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(redirectUrlCapture + "/" + saleId)
                .cancelUrl(redirectUrlCancel + "/" + saleId);
        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest()
                .requestBody(orderRequest);
        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = orderHttpResponse.result();
            String redirectUrl = order.links()
                    .stream()
                    .filter(linkDescription -> "approve".equals(linkDescription.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();
            return new PaymentOrder("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaymentOrder("error");
        }
    }

    public CompletedOrder completePayment(String token) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            if (httpResponse.result().status() != null) {
                PurchaseUnit purchaseUnit = httpResponse.result().purchaseUnits().get(0);
                Capture capture = purchaseUnit.payments().captures().get(0);
                String transactionId = capture.id();
                return new CompletedOrder("success", token, transactionId);
            }
        } catch (IOException e) {
            log.error("Error completing PayPal payment: {}", e.getMessage());
        }
        return new CompletedOrder("error");
    }

    public String refundPayment(String transactionId) {
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("USER", user);
            map.add("PWD", password);
            map.add("SIGNATURE", signature);
            map.add("METHOD", "RefundTransaction");
            map.add("VERSION", "94");
            map.add("TRANSACTIONID", transactionId);
            map.add("REFUNDTYPE", "Full");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForObject(refundUrl, requestEntity, String.class);
        } catch (Exception e) {
            log.error("Error completing PayPal payment: {}", e.getMessage());
            return "Error " + e.getMessage();
        }
    }

    public String payTo(Long batchId, String recipientEmail, Integer amount) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapRequestBody = buildRequestBody(batchId, recipientEmail, amount);
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(mapRequestBody);
        } catch (JsonProcessingException e) {
            return "Failed to generate JSON request body";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String accessToken = getAccessToken();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

        ResponseEntity<String> response = new RestTemplate().postForEntity(payUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            return "Payment sent successfully";
        } else {
            return "Failed to send payment. PayPal API returned : " + response.getStatusCodeValue();
        }
    }

    private Map<String, Object> buildRequestBody(Long batchId, String recipientEmail, Integer amount) {
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> senderBatchHeader = new HashMap<>();
        senderBatchHeader.put("sender_batch_id", String.valueOf(batchId));
        senderBatchHeader.put("recipient_type", "EMAIL");
        senderBatchHeader.put("email_subject", "Your product has been sold!");
        senderBatchHeader.put("email_message", "You received a payment for your product. Thanks for using our service!");

        Map<String, Object> item = new HashMap<>();
        Map<String, Object> itemAmount = new HashMap<>();
        itemAmount.put("value", String.valueOf(amount));
        itemAmount.put("currency", "USD");
        item.put("amount", itemAmount);
        item.put("sender_item_id", "1");
        item.put("recipient_wallet", "PAYPAL");
        item.put("receiver", recipientEmail);

        requestBody.put("sender_batch_header", senderBatchHeader);
        requestBody.put("items", Collections.singletonList(item));

        return requestBody;
    }

    private String getAccessToken() {
        String token_url = "https://api-m.sandbox.paypal.com/v1/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, secret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(token_url, HttpMethod.POST, requestEntity, String.class);

        String responseBody = response.getBody();
        String accessToken = null;
        if (responseBody != null) {
            accessToken = responseBody.split("\"access_token\":")[1].split(",")[0].replaceAll("\"", "").trim();
        }
        return accessToken;
    }
}
