package swp391.fa25.saleElectricVehicle.payload.request.user;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.UserStatus;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequest {
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;
    private String storeName;
    private String roleName;
}
