package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contracts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int contractId;

    @Column(unique = true)
    private String contractCode;

    @Column
    private ContractType contractType;

    @Column(nullable = false)
    private LocalDate contractDate;

    @Column(unique = true)
    private String contractFileUrl;

    @Column
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(columnDefinition = "DECIMAL(15,0)")
    private BigDecimal depositPrice;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal totalPayment;

    @Column(columnDefinition = "DECIMAL(15,0)")
    private BigDecimal remainPrice;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String terms;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String uploadedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

//    @ManyToOne
//    @JoinColumn(name = "orderId", nullable = false)
//    private Order order;

    @OneToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;
}
