package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfoDto {
    private String bankName; // Tên ngân hàng
    private String accountNumber; // Số tài khoản
    private String accountHolderName; // Tên chủ tài khoản
    private BigDecimal totalAmount; // Số tiền cần chuyển
    private String transactionCode; // Mã giao dịch (inventoryId)
    private String note; // Nội dung chuyển khoản
}

