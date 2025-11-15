package swp391.fa25.saleElectricVehicle.payload.response.store;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreMonthlyRevenueResponse {
    private int storeId;
    private String storeName;
    private String address;
    private BigDecimal monthlyRevenue; // Doanh thu tháng này
    private int orderCount; // Số lượng đơn hàng FULLY_PAID trong tháng
}

