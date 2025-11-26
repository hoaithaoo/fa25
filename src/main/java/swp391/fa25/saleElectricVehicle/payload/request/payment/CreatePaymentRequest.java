package swp391.fa25.saleElectricVehicle.payload.request.payment;

import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentMethod;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {
    int orderId;
    PaymentType paymentType;
    PaymentMethod paymentMethod;
}
