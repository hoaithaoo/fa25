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
    private Boolean isActive;  // ✅ Thêm field này vì ở UserServiceImpl có dùng
    private String phone;
    private String status;
    private int storeId;
    private int roleId;
}
