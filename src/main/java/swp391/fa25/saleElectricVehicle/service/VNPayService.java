package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentUrlRequest;

import java.math.BigDecimal;
import java.util.Map;

public interface VNPayService {
    String buildPaymentUrl(CreatePaymentUrlRequest request);
    Map<String, String> processIpn(Map<String, String> params);
//    boolean validateAmount(String paymentCode, BigDecimal amount);
}
