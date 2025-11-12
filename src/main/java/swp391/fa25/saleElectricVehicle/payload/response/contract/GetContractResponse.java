package swp391.fa25.saleElectricVehicle.payload.response.contract;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetContractResponse {
    private int contractId;
    private String contractCode;
    private LocalDate contractDate;
    private String status;
    private BigDecimal depositPrice;
    private BigDecimal totalPayment;
    private BigDecimal remainPrice;
    private String terms;
    private String contractFileUrl;

    private int orderId;
    private String orderCode;

    private int customerId;
    private String customerName;
    private LocalDateTime createdAt;
}
