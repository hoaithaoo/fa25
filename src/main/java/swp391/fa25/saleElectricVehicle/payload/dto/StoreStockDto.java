package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStockDto {
    private int stockId;
    private int storeId;
    private String storeName;
    private int modelId;
    private String modelName;
    private int colorId;
    private String colorName;
    private BigDecimal priceOfStore;
    private int quantity;
}