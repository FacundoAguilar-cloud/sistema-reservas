package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.merchantorder.MerchantOrderCreateRequest;
import com.mercadopago.client.merchantorder.MerchantOrderItemRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.request.QRPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

import jakarta.annotation.PostConstruct;

@Component
public class MercadoPagoQRProvider implements QRPaymentProvider {

private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MercadoPagoQRProvider.class);
    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accesstoken;
    @Value("${WEBHOOK_BASE_URL:http://localhost:8003}/api/webhooks/mercadopago")
    private String webhookUrl;

    private PaymentClient paymentClient;

    private MerchantOrderClient merchantOrderClient;

    @PostConstruct
    public void init(){
        if (accesstoken == null || accesstoken.trim().isEmpty()) {
            logger.error("MercadoPago access token not configured");
        }
        MercadoPagoConfig.setAccessToken(accesstoken);
        this.paymentClient = new PaymentClient();
        this.merchantOrderClient = new MerchantOrderClient();
    }

    @Override
    public PaymentProviderResponse processPayment(QRPaymentRequest request) {
      logger.info("Processing QR Payment{}", request.getAmount());

      try {
        validateQRRequest(request);

        MerchantOrder merchantOrder = createMerchantOrder(request);

        String  qrData = generateQrData(merchantOrder);
        

        return PaymentProviderResponse.builder()
        .success(true)
        .transactionId(merchantOrder.getId().toString())
        .qrCode(qrData)
        .paymentUrl(qrData)
        .status("PENDING")
        .message("QR generated correctly.")
        .amount(request.getAmount())
        .currency("ARS")
        .processedAt(LocalDateTime.now())
        .build();

      } catch (Exception e) {
        logger.error("Error processing QR payment.", e);
        return PaymentProviderResponse.builder()
        .success(false)
        .errorCode("MP_ERROR")
        .message("Error generating QR" + e.getMessage())
        .amount(request.getAmount())
        .currency(request.getCurrency())
        .processedAt(LocalDateTime.now())
        .build();
      }
    }

    @Override
    public String generateQRCode(BigDecimal amount, String description) {
        QRPaymentRequest request = new QRPaymentRequest(amount, description);
        PaymentProviderResponse response = processPayment(request);
        return response.isSuccess() ? response.getQrCode() : null;
    }

    @Override
    public PaymentProviderResponse checkQRPaymentStatus(String qrId) {
        try {
            MerchantOrder merchantOrder = merchantOrderClient.get(Long.valueOf(qrId));

            String status = determinePaymentStatus(merchantOrder);
            BigDecimal paidAmount = merchantOrder.getPaidAmount();
            return PaymentProviderResponse.builder()
            .success(true)
            .transactionId(qrId)
            .status("PENDING")
            .amount(paidAmount)
            .currency("ARS")
            .message("Status queried correctly.")
            .processedAt(LocalDateTime.now())
            .build();
        } catch (Exception e) {
            logger.error("Error checking QR payment status.", e);
            return PaymentProviderResponse.builder()
            .success(false)
            .errorCode("MP_STATUS_ERROR")
            .message("Error checking payment status." + e.getMessage())
            .build();
        }
        
        
    }

    @Override
    public String getProviderName() {
        return "Mercado Pago QR";
    }

    @Override
    public boolean isAvailable() {
        try {
            return accesstoken != null && !accesstoken.trim().isEmpty() && paymentClient != null && merchantOrderClient != null;
        } catch (Exception e) {
            logger.warn("Mercado Pago QR not available", e);
            return false;
        }
    }

    

    @Override //tener en cuenta que MP no permite cancelar pagos directamente, podriamos ver si esto hay que sacarlo o lo dejamos para darle otra utilidad
    public PaymentProviderResponse cancelQRPayment(String qrId) {
       return PaymentProviderResponse.builder()
       .success(true)
       .transactionId(qrId)
       .status("CANCELLED")
       .message("QR code successfully cancelled")
       .build();
    }

    @Override
    public boolean supportsPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.QR;
    }

    private void validateQRRequest(QRPaymentRequest request){
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be grater than zero.");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Desciption is required");
        }
    }
    private MerchantOrder createMerchantOrder(QRPaymentRequest request) throws MPException, MPApiException {
        MerchantOrderItemRequest item = MerchantOrderItemRequest.builder()
            .title(request.getTitle() != null ? request.getTitle() : request.getDescription())
            .description(request.getDescription())
            .quantity(1)
            .unitPrice(request.getAmount())
            .currencyId(request.getCurrency())
            .build();
            

        MerchantOrderCreateRequest.MerchantOrderCreateRequestBuilder builder = MerchantOrderCreateRequest.builder()
            .externalReference(request.getExternalReference())
            .items(List.of(item));

        if (request.getNotificationUrl() != null) {
            builder.notificationUrl(request.getNotificationUrl());
        } else if (webhookUrl != null) {
            builder.notificationUrl(webhookUrl);
        }   

        MerchantOrderCreateRequest orderRequest = builder.build();

        return merchantOrderClient.create(orderRequest);
    }

    private String generateQrData(MerchantOrder merchantOrder){
        //en produccion directamente se devuelven datos del QR, por ahora devolvemos una URL "simulada"
        return String.format("https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=%s",  merchantOrder.getId());
    }

    private QRPaymentRequest buildQRRequest(Payment payment){
        return QRPaymentRequest.builder()
        .transactionId(payment.getId() != null ? payment.getId().toString() : null)
        .amount(payment.getAmount())
        .currency(payment.getCurrency())
        .description(payment.getDescription())
        .title(payment.getDescription())
        .externalReference(payment.getExternalReference())
        .customerId(payment.getCustomerId())
        .expirationMinutes(30)
        .metaData(createQRMetadata(payment))
        .build();
    }

    private Map <String, String> createQRMetadata(Payment payment){
        Map <String, String> metadata = new HashMap<>();
        metadata.put("payment_id", payment.getId().toString());
        metadata.put("user_id", payment.getUserId().toString());
        metadata.put("appointment_id", payment.getAppointmentId().toString());
        metadata.put("shop_id", payment.getShopId().toString());
        metadata.put("payment_type", "qr");
        return metadata;
    }

    private String determinePaymentStatus(MerchantOrder merchantOrder){
        if (merchantOrder.getOrderStatus() == null) {
            return "PENDING";
        }
        switch (merchantOrder.getOrderStatus()) {
            case "paid":
                return "APPROVED";
            case "paymeny_required":
                return "PENDING";
            case "cancelled":    
                return "CANCELLED";
            case "expired":
                return "EXPIRED";    
        
            default:
                return "UNKNOWN";
        }
    }
    
}

