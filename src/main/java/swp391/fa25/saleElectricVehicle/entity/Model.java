package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name= "models")
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modelId;

    @Column(nullable = false, unique = true, columnDefinition = "nvarchar(255)")
    private String modelName;

    @Column(nullable = false)
    private int modelYear;

    @Column(columnDefinition = "DECIMAL(5,2)", nullable = false)
    private BigDecimal batteryCapacity;

    @Column(columnDefinition = "DECIMAL(5,2)", nullable = false)
    private BigDecimal range;

    @Column(columnDefinition = "DECIMAL(5,2)", nullable = false)
    private BigDecimal powerHp;

    @Column(columnDefinition = "DECIMAL(5,2)", nullable = false)
    private BigDecimal torqueNm;

    @Column(columnDefinition = "DECIMAL(5,2)", nullable = false)
    private BigDecimal acceleration;

    @Column(nullable = false)
    private int seatingCapacity;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String color;

    @Column(columnDefinition = "DECIMAL(10,2)", nullable = false)
    private BigDecimal price;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String bodyType;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreStock> storeStocks = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Promotion> promotions = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new java.util.ArrayList<>();
}
