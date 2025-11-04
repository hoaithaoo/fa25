package swp391.fa25.saleElectricVehicle.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface VNPayService {
    String buildPaymentUrl(int paymentId, HttpServletRequest request);
    Map<String, String> processIpn(Map<String, String> params);
//    boolean validateAmount(String paymentCode, BigDecimal amount);
}
