package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store_stocks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stockId;

    @Column(nullable = false)
    private BigDecimal priceOfStore;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "modelId")
    private Model model;

    @OneToMany(mappedBy = "storeStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryTransaction> inventoryTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "storeStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
