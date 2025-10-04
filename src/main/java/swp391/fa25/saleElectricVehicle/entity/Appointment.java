package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    public enum  AppointmentStatus {
        CONFIRMED,
        CANCELLED,
        NO_SHOW,
        IN_PROGRESS,
        COMPLETED
    }

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "modelId", nullable = false)
    private Model model;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "staffId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;
}
