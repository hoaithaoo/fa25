package swp391.fa25.saleElectricVehicle.payload.request.user;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserRequest {
    private String name;
}
