package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto {
    private int customerId;
    private String fullName;
    private String address;
    private String email;
    private String phone;
    private String identificationNumber;

    // trả về thông tin store để track customer của store nào
    private int storeId;
    private String storeName;
}
