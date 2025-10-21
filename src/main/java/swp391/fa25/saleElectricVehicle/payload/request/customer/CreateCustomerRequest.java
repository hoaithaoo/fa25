// payload/request/customer/CreateCustomerRequest.java
package swp391.fa25.saleElectricVehicle.payload.request.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomerRequest {

    @NotBlank(message = "Tên không được để trống")
    private String fullName;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    // ❌ KHÔNG có customerId (auto-increment)
    // ❌ KHÔNG có createdAt (backend tự gán)
}
