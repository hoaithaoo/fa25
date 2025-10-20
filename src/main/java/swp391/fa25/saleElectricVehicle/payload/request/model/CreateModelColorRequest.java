package swp391.fa25.saleElectricVehicle.payload.request.model;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateModelColorRequest {
    private int modelId;
    private int colorId;
    private String imagePath;
}