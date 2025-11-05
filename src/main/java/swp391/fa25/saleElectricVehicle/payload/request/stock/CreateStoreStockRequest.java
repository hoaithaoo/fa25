package swp391.fa25.saleElectricVehicle.payload.request.stock;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStoreStockRequest {
    private int modelId;
    private int colorId;
    private BigDecimal priceOfStore;
    private int quantity; // cho nhập để test tạo mới
}
