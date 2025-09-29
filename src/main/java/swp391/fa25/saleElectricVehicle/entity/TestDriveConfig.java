package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_drive_configs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestDriveConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int configId;

    @Column(nullable = false)
    private int maxAppointmentsPerDay;

    @Column(nullable = false)
    private int appointmentDurationMinutes;

    @Column(nullable = false)
    private int maxConcurrentAppointments;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @OneToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;
}
