package swp391.fa25.saleElectricVehicle.payload.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserRequest {
    private String name;
}
