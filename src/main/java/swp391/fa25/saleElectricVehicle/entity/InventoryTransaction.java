package swp391.fa25.saleElectricVehicle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryTransactionStatus status;

    @OneToMany(mappedBy = "inventoryTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "storeStockId", nullable = false)
    private StoreStock storeStock;

    @OneToOne(mappedBy = "inventoryTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private InventoryTransactionContract contract;

//    @ManyToOne
//    @JoinColumn(name = "promotionId", nullable = true)
//    private Promotion promotion; // Promotion của hãng được áp dụng (nếu có)
}
