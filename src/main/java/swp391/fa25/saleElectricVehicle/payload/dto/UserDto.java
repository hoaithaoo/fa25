package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String status;
    private int storeId;
    private int roleId;
    private String storeName;
    private String roleName;
}
