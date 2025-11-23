package swp391.fa25.saleElectricVehicle.payload.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String roleName;
    String email;
    boolean status;
}