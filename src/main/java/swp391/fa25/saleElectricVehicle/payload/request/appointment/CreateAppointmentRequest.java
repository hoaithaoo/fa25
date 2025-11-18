package swp391.fa25.saleElectricVehicle.payload.request.appointment;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.AppointmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAppointmentRequest {
    private int modelId;
    private int customerId;
    private LocalDateTime startTime;
}
