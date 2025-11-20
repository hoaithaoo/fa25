package swp391.fa25.saleElectricVehicle.payload.request.companybankaccount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompanyBankAccountRequest {
    @NotBlank(message = "Tên ngân hàng không được để trống")
    private String bankName;
    
    @NotBlank(message = "Số tài khoản không được để trống")
    private String accountNumber;
    
    @NotBlank(message = "Tên chủ tài khoản không được để trống")
    private String accountHolderName;
    
    @NotNull(message = "Trạng thái active không được để trống")
    private Boolean isActive;
}

