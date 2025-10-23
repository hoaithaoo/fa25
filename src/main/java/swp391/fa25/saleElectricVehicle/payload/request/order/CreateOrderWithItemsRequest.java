package swp391.fa25.saleElectricVehicle.payload.request.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderWithItemsRequest {
    private int orderId;
    List<CreateOrderDetailsRequest> orderDetails;
}
