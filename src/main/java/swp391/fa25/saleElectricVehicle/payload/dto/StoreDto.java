package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

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
    private String imagePath;
    private boolean isActive;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;
}
