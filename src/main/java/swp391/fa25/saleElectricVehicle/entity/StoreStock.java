package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store_stocks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stockId;

    @Column
    private BigDecimal priceOfStore;

    @Column(nullable = false)
    private int quantity;

    @Column
    @Builder.Default
    private int reservedQuantity = 0; // Số lượng đã được reserve bởi các orders CONFIRMED

    @ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "modelColorId")
    private ModelColor modelColor;

//    @Builder.Default
    @OneToMany(mappedBy = "storeStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryTransaction> inventoryTransactions = new ArrayList<>();

//    @Builder.Default
    @OneToMany(mappedBy = "storeStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

//    @OneToMany(mappedBy = "vin", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Vehicle> vehicles = new ArrayList<>();
}
