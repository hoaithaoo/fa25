package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modelColorId;

    @Column
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "modelId", nullable = false)
    private Model model;

    @ManyToOne
    @JoinColumn(name = "colorId", nullable = false)
    private Color color;

    @Column(columnDefinition = "DECIMAL(15,0)", nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "modelColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreStock> storeStocks = new ArrayList<>();
}
