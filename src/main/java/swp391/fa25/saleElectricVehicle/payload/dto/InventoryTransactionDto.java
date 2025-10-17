package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

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
    private int discountPercentage;
    private BigDecimal totalPrice;
    private int deposit;
    private BigDecimal dept;
    private LocalDateTime transactionDate;
    private LocalDateTime deliveryDate;
    private int storeStockId;
}
