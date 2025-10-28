package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private int orderId;
    private String orderCode;

    private List<OrderDetailsDto> orderDetailsList;

    private int totalQuantity;
    private BigDecimal totalUnitPrice; // đã gồm VAT
    private BigDecimal totalDiscount;
    private BigDecimal totalTaxPrice;

    // lấy id để trả về customer để đưa vào hợp đồng
    private int customerId;
    private String customerName;

    private int staffId;
    private String staffName;

    private int storeId;
    private String storeName;
    private String storeAddress;
}