package swp391.fa25.saleElectricVehicle.payload.request.vehicle;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleRequest {
    private String vin;
    private String engineNo;
    private String batteryNo;
}
