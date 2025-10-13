package swp391.fa25.saleElectricVehicle.payload.request.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private int storeId;
    private int roleId;
//    private String storeName;
//    private String roleName;
}
