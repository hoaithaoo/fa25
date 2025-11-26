package swp391.fa25.saleElectricVehicle.payload.request.payment;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmCashPaymentRequest {
    private BigDecimal amount;
    private String transactionRef;
    private String bankTransactionCode; // Optional, có thể null
}

