package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalTaxPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalPromotionAmount;

    @Column(precision = 15, scale = 2, nullable = false)
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

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Feedback feedback;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new java.util.ArrayList<>();
}