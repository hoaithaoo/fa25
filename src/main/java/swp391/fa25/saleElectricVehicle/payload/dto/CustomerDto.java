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
}
