package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDto {
    private int contractId;
    private LocalDate contractDate;
    private String contractFileUrl;
    private Contract.ContractStatus status;
    private BigDecimal depositPrice;
    private BigDecimal totalPayment;
    private BigDecimal remainPrice;
    private String terms;
    private String uploadedBy;
    private String createdAt;
    private String updatedAt;
    private int orderId; // Chỉ trả về orderId thay vì toàn bộ Order object
}
