package swp391.fa25.saleElectricVehicle.payload.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.UserStatus;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserStatusRequest {
    @NotNull(message = "Status is required")
    private UserStatus status;
}

