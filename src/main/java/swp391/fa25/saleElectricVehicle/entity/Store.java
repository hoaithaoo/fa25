package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int storeId;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Pattern(regexp = "^0\\d{9}$", message = "Phone không hợp lệ")
    private String phone;

    @Column(nullable = false)
    private String provinceName;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreStatus status = StoreStatus.ACTIVE;

    public enum StoreStatus {
        ACTIVE,
        INACTIVE;
    }

    @Column(nullable = false)
    private LocalDateTime contractStartDate;

    @Column(nullable = false)
    private LocalDateTime contractEndDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<User> user;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Appointment> appointments = new java.util.ArrayList<>();

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL)
    private TestDriveConfig testDriveConfig;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Promotion> promotions = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StoreStock> storeStocks = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SaleTarget> saleTargets = new java.util.ArrayList<>();
}
