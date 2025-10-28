package swp391.fa25.saleElectricVehicle.payload.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetOrderResponse {
    private int orderId;
    private String orderCode;

    private List<GetOrderDetailsResponse> getOrderDetailsResponses;

    private BigDecimal totalPrice;
    private BigDecimal totalTaxPrice;
    private BigDecimal totalPromotionAmount;
    private BigDecimal totalPayment;
    private String status;

    private int contractId;
    private String contractCode; // For display

    private int customerId;
    private String customerName;  // For display
    private String customerPhone;

    private int feedbackId;

    private int staffId;
    private String staffName;

    private int storeId;
    private String storeName;

    private LocalDateTime orderDate;
}
