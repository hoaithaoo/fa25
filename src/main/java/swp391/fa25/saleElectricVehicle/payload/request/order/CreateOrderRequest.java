package swp391.fa25.saleElectricVehicle.payload.request.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    private int customerId;
}
