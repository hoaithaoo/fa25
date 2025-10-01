package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private int roleId;
    private String roleName;
}
