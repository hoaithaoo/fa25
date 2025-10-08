package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStockDto {

    private int stockId;
    private String storeName;
    private String modelName;
    private String colorName;
    private BigDecimal priceOfStore;
    private int quantity;

    // Foreign Keys
//    private int storeId;
//    private int modelColorId;
}