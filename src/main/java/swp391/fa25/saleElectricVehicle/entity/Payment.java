package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentMethod;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @Column
    private String paymentCode;

    @Column(columnDefinition = "DECIMAL(15,0)")
    private BigDecimal remainPrice;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(columnDefinition = "nvarchar(255)")
    private PaymentMethod paymentMethod;

    @Column(columnDefinition = "DECIMAL(15,0)")
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
}