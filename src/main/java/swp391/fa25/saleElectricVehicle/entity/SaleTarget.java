package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_targets")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int targetId;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private BigDecimal achievedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleTargetStatus status;

    public enum SaleTargetStatus {
        MET,
        NOT_MET
    }

    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;
}
