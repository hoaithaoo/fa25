package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Payment;

public interface PaymentService {
    Payment getPaymentEntityById(int paymentId);
    Payment getPaymentEntityByPaymentCode(String paymentCode);
}
