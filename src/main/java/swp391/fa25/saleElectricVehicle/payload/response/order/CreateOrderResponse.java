package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponse {
    private int orderId;
    private BigDecimal totalPrice;
    private BigDecimal totalTaxPrice;
    private BigDecimal totalPromotionAmount;
    private BigDecimal totalPayment;

    private int customerId;
    private String customerName;  // For display
    private String customerPhone;

    private int staffId;
    private String staffName;

    private int storeId;
    private String storeName;

    private LocalDateTime orderDate;
}
