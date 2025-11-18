package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "test_drive_configs", uniqueConstraints = {
        @UniqueConstraint(columnNames = "store_Id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDriveConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer configId;

    @Column(nullable = false)
    private int appointmentDurationMinutes;

    @Column(nullable = false)
    private int maxAppointmentsPerModelPerSlot;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @OneToOne
    @JoinColumn(name = "store_Id", nullable = false)
    private Store store;
}
