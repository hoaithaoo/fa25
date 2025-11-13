package swp391.fa25.saleElectricVehicle.payload.request.inventory;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInventoryTransactionRequest {
    private int modelId;
    private int colorId;
    private int importQuantity;
}
