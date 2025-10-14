package swp391.fa25.saleElectricVehicle.payload.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private int orderId;
    private BigDecimal totalPrice;
    private BigDecimal totalTaxPrice;
    private BigDecimal totalPromotionAmount;
    private BigDecimal totalPayment;
    private OrderStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Foreign keys
    private int customerId;
    private String customerName;  // For display
    private String customerPhone; // For display

    private int staffId;
    private String staffName;     // For display

    private int contractId;       // If needed

    // Order details (optional, có thể separate endpoint)
    private List<OrderDetailDto> orderDetails;

    // Calculated fields
    private int totalItems;       // Tổng số items
    private String statusDisplay; // For frontend display
}