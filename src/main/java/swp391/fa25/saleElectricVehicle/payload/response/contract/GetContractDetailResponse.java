package swp391.fa25.saleElectricVehicle.payload.response.contract;

import lombok.*;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailsDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetContractDetailResponse {
    // Contract information
    private int contractId;
    private String contractCode;
    private LocalDate contractDate;
    private String status;
    private BigDecimal depositPrice;
    private BigDecimal totalPayment;
    private BigDecimal remainPrice;
    private String terms;
    private String contractFileUrl;
    private String uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Order information
    private int orderId;
    private String orderCode;
    private String orderStatus;
    private LocalDateTime orderDate;
    private BigDecimal orderTotalPrice;
    private BigDecimal orderTotalTaxPrice;
    private BigDecimal orderTotalPromotionAmount;
    private BigDecimal orderTotalPayment;
    
    // Customer information
    private CustomerDto customer;
    
    // Staff information
    private int staffId;
    private String staffName;
    
    // Store information
    private int storeId;
    private String storeName;
    private String storeAddress;
    
    // Order details
    private List<OrderDetailsDto> orderDetails;
    
    // Payments
    private List<PaymentInfo> payments;
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentInfo {
        private int paymentId;
        private String paymentCode;
        private BigDecimal remainPrice;
        private String status;
        private String paymentType;
        private String paymentMethod;
        private BigDecimal amount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

