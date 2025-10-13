package swp391.fa25.saleElectricVehicle.payload.response.user;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.UserStatus;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileResponse {
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;
    private int storeId;
    private String storeName;
    private int roleId;
    private String roleName;
}
