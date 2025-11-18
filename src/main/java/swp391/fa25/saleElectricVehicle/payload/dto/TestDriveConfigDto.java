package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDriveConfigDto {
    private Integer configId;
    private int appointmentDurationMinutes;
    private int maxAppointmentsPerModelPerSlot;
    private LocalTime startTime;
    private LocalTime endTime;
    private int storeId; // Liên kết với Store
}
