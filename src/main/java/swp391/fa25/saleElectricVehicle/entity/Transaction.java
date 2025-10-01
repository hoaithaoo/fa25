package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @Column(columnDefinition = "DECIMAL(12,2)", nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionTime;

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String gateway;

    @Column(unique = true)
    private String transactionRef;

    @Column
    private String payerInfor;

    @Column(columnDefinition = "nvarchar(255)")
    private String note;

    @ManyToOne
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;
}
