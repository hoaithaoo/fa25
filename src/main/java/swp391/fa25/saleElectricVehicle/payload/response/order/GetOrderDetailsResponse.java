package swp391.fa25.saleElectricVehicle.payload.response.order;

import lombok.*;
import swp391.fa25.saleElectricVehicle.payload.dto.VehicleDto;

import java.math.BigDecimal;
import java.util.List;

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

    private BigDecimal licensePlateFee; // phí biển số
    private BigDecimal serviceFee; // phí đăng ký biển số
    private BigDecimal otherTax; // thuế khác
    private BigDecimal otherFees; // phí khác (gồm phí đăng ký biển số + thuế khác = serviceFee + otherTax)

    private Integer promotionId;
    private String promotionName;
    private BigDecimal discountAmount;

    private BigDecimal totalPrice;

    // Danh sách vehicles được gán vào order detail này (nếu có)
    private List<VehicleDto> vehicles;
}
