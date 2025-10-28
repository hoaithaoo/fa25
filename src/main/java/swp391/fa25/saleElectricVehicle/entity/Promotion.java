package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PromotionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int promotionId;

    @Column(nullable = false, columnDefinition = "nvarchar(255)", unique = true)
    private String promotionName;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private PromotionType promotionType;

    @Column(columnDefinition = "DECIMAL(15,0)")
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

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
