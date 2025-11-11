package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.StoreStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private int storeId;
    private String storeName;
    private String address;
    private String phone;
    private String provinceName;
    private String ownerName;
    private StoreStatus status;
    private String imagePath;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
}
