package swp391.fa25.saleElectricVehicle.service;

import java.util.List;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetPaymentResponse;

import java.math.BigDecimal;

public interface PaymentService {
    GetPaymentResponse createDraftPayment(CreatePaymentRequest request);
    GetPaymentResponse getPaymentById(int paymentId);
    Payment getPaymentEntityById(int paymentId);
    Payment getPaymentEntityByPaymentCode(String paymentCode);
    List<GetPaymentResponse> getAllPaymentsByStore();
    void updatePaymentStatus(Payment payment, BigDecimal amount, PaymentStatus status);
}
