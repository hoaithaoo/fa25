package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.request.payment.PaymentRequest;

public interface VNPayService {
    String buildPaymentUrl(PaymentRequest paymentRequest);
}
