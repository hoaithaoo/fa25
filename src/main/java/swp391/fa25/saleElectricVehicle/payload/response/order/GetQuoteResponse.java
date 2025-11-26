package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetQuoteResponse {
    private int orderId;
    private String orderCode;

    private List<GetOrderDetailsResponse> getOrderDetailsResponses;

    private BigDecimal totalPrice;
    private BigDecimal totalLicensePlateFee; // tổng phí biển số
    private BigDecimal totalServiceFee; // tổng phí đăng ký biển số
    private BigDecimal totalOtherTax; // tổng thuế khác
    private BigDecimal totalOtherFees; // tổng phí khác (gồm phí đăng ký biển số + thuế khác = totalServiceFee + totalOtherTax)
    private BigDecimal totalPromotionAmount;
    private BigDecimal totalPayment;
    private String status;
}
