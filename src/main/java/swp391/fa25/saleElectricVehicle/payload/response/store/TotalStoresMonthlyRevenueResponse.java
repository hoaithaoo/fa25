package swp391.fa25.saleElectricVehicle.payload.response.store;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalStoresMonthlyRevenueResponse {
    private int year;
    private int month;
    private long totalOrders; // Tổng số đơn hàng FULLY_PAID của tất cả store trong tháng
    private BigDecimal totalRevenue; // Tổng doanh thu của tất cả store trong tháng
}

