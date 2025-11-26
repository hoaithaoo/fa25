package swp391.fa25.saleElectricVehicle.payload.response.contract;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractPaymentInfo {
    private int paymentId;
    private String paymentCode;
    private String status;
    private String paymentType;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

