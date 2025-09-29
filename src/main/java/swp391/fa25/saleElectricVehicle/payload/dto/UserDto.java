package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private Boolean isActive;
    private int storeId;
    private String storeName;
    private String roleName;
}
