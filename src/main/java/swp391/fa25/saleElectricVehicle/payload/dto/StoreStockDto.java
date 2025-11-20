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
    private BigDecimal basePrice; // Giá gốc từ ModelColor
    private BigDecimal priceOfStore; // Giá bán của cửa hàng
    private int quantity; // Tổng số lượng trong kho
    private int availableStock; // Số lượng có thể bán = quantity - reservedQuantity (cho khách hàng)
}