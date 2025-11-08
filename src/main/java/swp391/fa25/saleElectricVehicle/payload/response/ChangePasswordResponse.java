package swp391.fa25.saleElectricVehicle.payload.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordResponse {
    private String message;
    private String status;
}

