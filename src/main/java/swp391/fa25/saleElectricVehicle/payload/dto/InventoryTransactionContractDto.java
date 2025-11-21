package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionContractDto {
    private int contractId;
    private String contractCode;
    private LocalDate contractDate;
    private String contractFileUrl;
    private String status;
    private String uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int inventoryId;
}

