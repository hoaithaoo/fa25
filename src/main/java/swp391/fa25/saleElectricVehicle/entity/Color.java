package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colors")
@NoArgsConstructor
@AllArgsConstructor
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer colorId;

    @Column(nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private String colorName;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModelColor> modelColors = new ArrayList<>();
}
