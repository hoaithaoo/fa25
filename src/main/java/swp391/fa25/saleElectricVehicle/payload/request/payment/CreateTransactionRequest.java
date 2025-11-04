package swp391.fa25.saleElectricVehicle.payload.request.payment;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.Transaction;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentGateway;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionRequest {
//    private int paymentId;
    private String paymentCode;
    private String transactionRef;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String bankTransactionCode;
    private PaymentGateway gateway;
//    private String payerInfor;
//    private String note;
    private TransactionStatus status;
}
