package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    private long vehicleId;
    private String vin;
    private String engineNo;
    private String batteryNo;
    private String status;
    private LocalDateTime importDate;
    private LocalDateTime saleDate;
    private String notes;

    private int inventoryTransaction;
}
