package swp391.fa25.saleElectricVehicle.payload.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swp391.fa25.saleElectricVehicle.entity.Store;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private int storeId;
    private String storeName;
    private String address;
    private String phone;
    private String provinceName;
    private String ownerName;
    private Store.StoreStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime contractStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime contractEndDate;
}
