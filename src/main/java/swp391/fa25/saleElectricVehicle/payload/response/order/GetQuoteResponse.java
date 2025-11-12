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
    private BigDecimal totalLicensePlateFee;
    private BigDecimal totalRegistrationFee;
//    private BigDecimal totalTaxPrice;
    private BigDecimal totalPromotionAmount;
    private BigDecimal totalPayment;
    private String status;
}
