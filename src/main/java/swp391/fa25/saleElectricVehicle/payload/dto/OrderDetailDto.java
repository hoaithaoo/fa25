//package swp391.fa25.saleElectricVehicle.payload.dto;
//
//import lombok.*;
//import java.math.BigDecimal;
//
//@Getter @Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class OrderDetailDto {
//    private int orderDetailId;
//    private int quantity;
//    private BigDecimal unitPrice;
//    private BigDecimal totalPrice;
//
//    // Foreign keys
//    private int orderId;
//    private int modelId;
//    private String modelName;     // For display
//    private int colorId;
//    private String colorName;     // For display
//}

package swp391.fa25.saleElectricVehicle.payload.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {

    private int id;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal vatAmount;
    private BigDecimal licensePlateFee;
    private BigDecimal registrationFee;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Foreign Keys
    private int orderId;
    private int promotionId;
    private int storeStockId;

    // Display fields (from relationships)
    private String modelName;       // From StoreStock -> ModelColor -> Model
    private String colorName;       // From StoreStock -> ModelColor -> Color
    private BigDecimal modelPrice;  // From StoreStock -> priceOfStore
    private int availableStock;     // From StoreStock -> quantity

    // Order info
    private String orderStatus;     // From Order
    private String customerName;    // From Order -> Customer
    private String customerPhone;   // From Order -> Customer

    // Promotion info
    private String promotionName;   // From Promotion
    private String promotionType;   // From Promotion

    // Calculated fields - Electric Vehicle specific
    private BigDecimal subtotal;           // unitPrice * quantity
    private BigDecimal totalFees;          // licensePlateFee + registrationFee
    private BigDecimal totalTax;           // vatAmount
    private BigDecimal priceBeforeDiscount; // subtotal + fees + tax
    private BigDecimal finalAmount;        // totalPrice (same as totalPrice)

    // Display info
    private String displayText;            // "Model X - Color Red (Qty: 2)"
    private String feeBreakdown;           // "License: $X, Registration: $Y, VAT: $Z"
    private String priceBreakdown;         // Detailed price calculation
}