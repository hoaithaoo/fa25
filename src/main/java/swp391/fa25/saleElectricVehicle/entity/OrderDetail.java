package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_details")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

//    @Column(nullable = false)
//    private BigDecimal vatAmount;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal licensePlateFee;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal registrationFee;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal discountAmount;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @OneToOne
    @JoinColumn(name = "vehicleId", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "storeStockId", nullable = false)
    private StoreStock storeStock;
}
