package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyBankAccountDto {
    private int accountId;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

