package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetOrderDetailsResponse {
    private int orderDetailId;

    private int modelId;
    private String modelName;

    private int colorId;
    private String colorName;

    private BigDecimal unitPrice;
    private int quantity;

    private BigDecimal vatAmount;
    private BigDecimal licensePlateFee;
    private BigDecimal registrationFee;

    private Integer promotionId;
    private String promotionName;
    private BigDecimal discountAmount;

    private BigDecimal totalPrice;
}
