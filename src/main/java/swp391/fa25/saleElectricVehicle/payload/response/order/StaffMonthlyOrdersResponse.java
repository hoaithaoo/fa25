package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffMonthlyOrdersResponse {
    private int staffId;
    private String staffName;
    private List<GetOrderResponse> orders; // Danh sách orders trong tháng
    private int totalOrders; // Tổng số orders trong tháng
    private BigDecimal monthlyRevenue; // Doanh thu tháng (tổng totalPayment)
}

