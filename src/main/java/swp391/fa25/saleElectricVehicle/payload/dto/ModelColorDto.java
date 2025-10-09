package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelColorDto {
    private int modelColorId;
    private String modelName;
    private String colorName;
}
