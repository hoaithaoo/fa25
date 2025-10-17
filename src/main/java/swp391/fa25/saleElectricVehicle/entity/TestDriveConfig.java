package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_drive_configs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDriveConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer configId;

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
    @JoinColumn(name = "store_Id", nullable = false)
    private Store store;
}
