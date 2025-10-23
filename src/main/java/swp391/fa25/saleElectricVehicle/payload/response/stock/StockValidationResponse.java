package swp391.fa25.saleElectricVehicle.payload.response.stock;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockValidationResponse {
    private int modelId;
    private String modelName;
    private int colorId;
    private String colorName;
    private int requestedQuantity;
    private int promotionId;
    private String promotionName;
//    private int availableStock;
//    private boolean isAvailable;
}
