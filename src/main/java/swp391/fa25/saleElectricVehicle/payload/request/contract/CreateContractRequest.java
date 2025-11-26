package swp391.fa25.saleElectricVehicle.payload.request.contract;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractType;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateContractRequest {
    private int orderId;
    private ContractType contractType;
}
