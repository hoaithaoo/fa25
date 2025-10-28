package swp391.fa25.saleElectricVehicle.payload.request.contract;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateContractRequest {
    private int orderId;
}
