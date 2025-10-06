package swp391.fa25.saleElectricVehicle.payload.request.user;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOwnProfileUserRequest {
    private String fullName;
    private String email;
    private String phone;
}
