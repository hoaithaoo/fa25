package swp391.fa25.saleElectricVehicle.payload.response.payment;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentMethod;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPaymentResponse {
    private int paymentId;
    private String paymentCode;
    private BigDecimal remainPrice;
    private PaymentStatus status;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String contractCode;
}
