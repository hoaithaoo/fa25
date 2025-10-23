package swp391.fa25.saleElectricVehicle.payload.request.stock;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockValidationRequest {
    private int modelId;
    private int colorId;
    private int quantity;
    private int promotionId;
}
