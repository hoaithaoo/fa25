package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDto {
    private int contractId;
    private String contractCode;
    private LocalDate contractDate;
    private String status;
    private BigDecimal depositPrice;
    private BigDecimal totalPayment;
    private BigDecimal remainPrice;
    private String terms;

//    private String customerName;
//    private String customerAddress;
//    private String customerPhone;
//    private String customerEmail;
//    private String customerIdentificationNumber;
//    private String contractFileUrl;
//    private String uploadedBy;
//    private String createdAt;
//    private String updatedAt;
    private int orderId; // Chỉ trả về orderId thay vì toàn bộ Order object
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
