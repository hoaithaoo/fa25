package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int inventoryId;

    @Column(nullable = false)
    private BigDecimal unitBasePrice;

    @Column(nullable = false)
    private int importQuantity;

    @Column
    private int discountPercentage;

    @Column(nullable = false)
    private BigDecimal totalPrice;

//    @Column(nullable = false)
//    private int deposit;
//
//    @Column
//    private BigDecimal dept;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column
    private LocalDateTime deliveryDate;

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryTransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "storeStockId", nullable = false)
    private StoreStock storeStock;

//    @ManyToOne
//    @JoinColumn(name = "promotionId", nullable = true)
//    private Promotion promotion; // Promotion của hãng được áp dụng (nếu có)
}
