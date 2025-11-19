package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreMonthlyRevenueResponse {
    private int storeId;
    private String storeName;
    private String address;
    private long totalOrders; // Tổng số đơn hàng FULLY_PAID trong tháng hiện tại
    private BigDecimal totalRevenue; // Tổng doanh thu từ các đơn hàng FULLY_PAID trong tháng hiện tại
}

