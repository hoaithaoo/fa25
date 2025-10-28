package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.*;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderDetailsRequest;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDetailsResponse {
    private int orderDetailId;

    private int modelId;
    private String modelName;

    private int colorId;
    private String colorName;

    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;

//    private BigDecimal vatAmount;
    private BigDecimal licensePlateFee;
    private BigDecimal registrationFee;

    private Integer promotionId;
    private String promotionName;
    private BigDecimal discountAmount;

}
