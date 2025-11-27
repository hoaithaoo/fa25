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
//    private boolean includeLicensePlateService; // true nếu khách chọn dịch vụ đăng ký biển số, false hoặc null nếu không chọn
}
