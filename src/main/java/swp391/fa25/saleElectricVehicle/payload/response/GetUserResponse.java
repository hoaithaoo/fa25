package swp391.fa25.saleElectricVehicle.payload.response;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.UserStatus;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserResponse {
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;
    private int storeId;
    private int roleId;
}
