package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStockDto {

    private int stockId;
    private BigDecimal priceOfStore;
    private int quantity;

    // Foreign Keys
    private int storeId;
    private int modelColorId;

    // Display fields (from relationships)
    private String storeName;     // From Store
    private String storeAddress;  // From Store
    private String modelName;     // From ModelColor -> Model
    private String colorName;     // From ModelColor -> Color
    private String brandName;     // From ModelColor -> Model -> Brand

    // Calculated fields
    private BigDecimal totalValue;      // priceOfStore * quantity
    private String stockStatus;         // "In Stock", "Low Stock", "Out of Stock"
    private boolean isAvailable;        // quantity > 0
    private String displayText;         // "Model X - Red (Store: Hanoi)"
}