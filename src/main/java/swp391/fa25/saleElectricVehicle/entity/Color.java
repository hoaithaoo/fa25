package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colors")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int colorId;

    @Column(nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private String colorName;

    @Column(nullable = false, unique = true, columnDefinition = "varchar(7)")
    private String colorCode;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModelColor> modelColors = new ArrayList<>();
}
