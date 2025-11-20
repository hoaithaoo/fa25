package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionContractStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transaction_contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int contractId;

    @Column(unique = true)
    private String contractCode;

    @Column(nullable = false)
    private LocalDate contractDate;

    @Column(unique = true)
    private String contractFileUrl; // File cuối cùng (có cả 2 chữ ký) - Manager upload

    @Column
    private String evmSignatureUrl; // URL ảnh chữ ký EVM (để generate HTML khi cần)

    @Column
    @Enumerated(EnumType.STRING)
    private InventoryTransactionContractStatus status;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String uploadedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "inventoryId", nullable = false, unique = true)
    private InventoryTransaction inventoryTransaction;
}

