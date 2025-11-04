package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.request.payment.PaymentRequest;

import java.math.BigDecimal;

public interface VNPayService {
    String buildPaymentUrl(PaymentRequest paymentRequest);
    boolean validateAmount(String paymentCode, BigDecimal amount);
}
