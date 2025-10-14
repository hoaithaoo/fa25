package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.Appointment;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
    private int appointmentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Appointment.AppointmentStatus status;
    private LocalDateTime createdAt;
    private int modelId;
    private int customerId;
    private int staffId;
    private int storeId;
}
