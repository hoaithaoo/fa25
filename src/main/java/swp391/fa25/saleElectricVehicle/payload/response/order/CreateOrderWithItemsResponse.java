package swp391.fa25.saleElectricVehicle.payload.response.order;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderWithItemsResponse {
    private List<CreateOrderDetailsResponse> orderDetailsResponses = new ArrayList<>();
}
