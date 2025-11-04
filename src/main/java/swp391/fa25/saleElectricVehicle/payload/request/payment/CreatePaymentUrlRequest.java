package swp391.fa25.saleElectricVehicle.payload.request.payment;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentUrlRequest {
//    private int contractId;
    private int paymentId;
}
