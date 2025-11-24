package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Column(unique = true)
    private String orderCode;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal totalPrice;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal totalTaxPrice;

    @Column(columnDefinition = "DECIMAL(15,0)")
    private BigDecimal totalPromotionAmount;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal totalPayment;

    @Enumerated(EnumType.STRING)
    @Column
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id")
    private Contract contract;

//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Contract> contracts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private User user;

    // Store snapshot - lưu store tại thời điểm tạo order
    // Store luôn được set từ user.store khi tạo order (validation ở service layer)
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // Tự động set store từ user.store trước khi persist (đảm bảo consistency)
    // Luôn override store bằng user.store để tránh inconsistency
    @PrePersist
    private void setStoreFromUser() {
        if (this.user != null && this.user.getStore() != null) {
            this.store = this.user.getStore();
        }
    }

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Feedback feedback;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new java.util.ArrayList<>();
}