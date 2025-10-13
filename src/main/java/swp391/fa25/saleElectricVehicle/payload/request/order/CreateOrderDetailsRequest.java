package swp391.fa25.saleElectricVehicle.payload.request.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDetailsRequest {
    private int orderId;
    private int modelId;
    private int colorId;
    private int quantity;
    private Integer promotionId;
}
