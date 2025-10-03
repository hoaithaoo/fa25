package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modelColorId;

    @ManyToOne
    @JoinColumn(name = "modelId", nullable = false)
    private Model model;

    @ManyToOne
    @JoinColumn(name = "colorId", nullable = false)
    private Color color;

    @OneToMany(mappedBy = "modelColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreStock> storeStocks = new ArrayList<>();
}
