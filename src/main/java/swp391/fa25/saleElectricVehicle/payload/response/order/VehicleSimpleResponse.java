package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSimpleResponse {
    private long vehicleId;
    private String vin;
    private String engineNo; // số máy
    private String batteryNo; // số pin
    private String status;
}

