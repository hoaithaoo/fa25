package swp391.fa25.saleElectricVehicle.payload.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.Store;

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
    private Store.StoreStatus status;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;
}
