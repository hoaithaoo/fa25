package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionDto {
    private int inventoryId;

    private BigDecimal unitBasePrice;
    private int importQuantity;
    private BigDecimal totalBasePrice;

    private int discountPercentage;
    private BigDecimal discountAmount;

    private BigDecimal totalPrice;
//    private int deposit;
//    private BigDecimal dept;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate; // ngày giao hàng
    private String imageUrl; // URL biên lai thanh toán
    private InventoryTransactionStatus status;

    private int modelId;
    private String modelName;

    private int colorId;
    private String colorName;
//    private int storeStockId;
//    private Integer promotionId; // Promotion của hãng được áp dụng (nếu có)
}
