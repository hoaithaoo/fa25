package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelColorDto {
    private int modelColorId;
    private int modelId;
    private String modelName;
    private int colorId;
    private String colorName;
    private String imagePath;
}
