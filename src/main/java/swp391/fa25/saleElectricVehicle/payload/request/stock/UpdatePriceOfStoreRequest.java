package swp391.fa25.saleElectricVehicle.payload.request.stock;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePriceOfStoreRequest {
    private int modelId;
    private int colorId;
    private BigDecimal price;
}
