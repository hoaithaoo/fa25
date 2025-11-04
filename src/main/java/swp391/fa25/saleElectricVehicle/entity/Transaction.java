package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionTime;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String gateway;

    @Column(unique = true)
    private String transactionRef;

    @Column(unique = true)
    private String bankTransactionCode;

//    @Column
//    private String payerInfor;

//    @Column(columnDefinition = "nvarchar(255)")
//    private String note;

    @ManyToOne
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;
}
