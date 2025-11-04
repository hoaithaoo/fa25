package swp391.fa25.saleElectricVehicle.payload.response.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetTransactionResponse {
    private int transactionId;
    private String transactionRef;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String gateway;
//    private String payerInfor;
    private String note;
    private String status;
}
