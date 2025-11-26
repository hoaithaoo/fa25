package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(columnDefinition = "VARCHAR(17)", nullable = false, unique = true)
    private String vin;

    @Column(nullable = false, unique = true, length = 6)
    private String engineNo; // số máy

    @Column(nullable = false, unique = true, length = 10)
    private String batteryNo; // số seri pin

    @Column
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @Column
    private LocalDateTime importDate; // ngày nhập xe

    @Column
    private LocalDateTime saleDate; // ngày bán xe

    @Column
    private String notes;

    @ManyToOne
    @JoinColumn(name = "inventoryId")
    private InventoryTransaction inventoryTransaction;

    @ManyToOne
    @JoinColumn(name = "stockId")
    private StoreStock storeStock;

    // 1 vehicle thuộc về 1 order detail (nhiều vehicle có thể cùng 1 detail)
    @ManyToOne
    @JoinColumn(name = "orderDetailId")
    private OrderDetail orderDetail;

    // gán stock vào ngay khi tạo
    @PrePersist
    private void setStockFromInventory() {
        if (this.inventoryTransaction != null && this.inventoryTransaction.getStoreStock() != null) {
            this.storeStock = this.inventoryTransaction.getStoreStock();
        }
    }
}
