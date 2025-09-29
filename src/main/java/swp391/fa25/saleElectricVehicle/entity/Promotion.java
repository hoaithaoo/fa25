package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int promotionId;

    @Column(nullable = false)
    private String promotionName;

    @Column(nullable = false, length = 500)
    private String description;

    @Column
    private PromotionType promotionType;

    public enum  PromotionType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    @Column
    private BigDecimal amount;

    @Column(nullable = false)
    private boolean startDate;

    @Column(nullable = false)
    private boolean endDate;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "modelId", nullable = false)
    private Model model;

    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
