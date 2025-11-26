package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDto {
    // Contract information
    private int contractId;
    private String contractCode;
    private String contractType;
    private LocalDate contractDate;
    private String status;
    private BigDecimal totalPayment;
    private String contractFileUrl;
    private String uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Order information
    private int orderId;
    private String orderCode;
    // private String orderStatus;
    // private LocalDateTime orderDate;
    // private BigDecimal orderTotalPrice;
    // private BigDecimal orderTotalTaxPrice;
    // private BigDecimal orderTotalPromotionAmount;
    // private BigDecimal orderTotalPayment;
    
    // Customer information (simple)
    private int customerId;
    private String customerName;
    
    // Customer information (detailed - optional, for detail view)
    // private CustomerDto customer;
    
    // Staff information
    private int staffId;
    private String staffName;
    
    // Store information
    private Integer storeId;
    private String storeName;
    private String storeAddress;
    
    // Order details (optional - for detail view)
    // private List<OrderDetailsDto> orderDetails;
    
    // Payments (optional - for detail view)
    // private List<ContractPaymentInfo> payments;
}
