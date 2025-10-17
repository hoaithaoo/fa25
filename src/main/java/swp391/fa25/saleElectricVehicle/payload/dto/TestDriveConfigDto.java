package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDriveConfigDto {
    private Integer configId;
    private int maxAppointmentsPerDay;
    private int appointmentDurationMinutes;
    private int maxConcurrentAppointments;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int storeId; // Liên kết với Store
}
