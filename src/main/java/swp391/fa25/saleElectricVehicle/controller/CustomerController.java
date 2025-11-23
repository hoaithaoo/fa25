package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createCustomer(
            @Valid @RequestBody CustomerDto request) {
        // Nhân viên tạo thông tin khách hàng: lưu vào Redis và gửi email verification
        String verificationToken = customerService.createCustomer(request);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.CREATED.value())
                .message("Thông tin khách hàng đã được tạo và email xác thực đã được gửi đến khách hàng. Vui lòng yêu cầu khách hàng kiểm tra email để xác thực.")
                .data(verificationToken)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponse<String>> resendVerificationEmail(
            @RequestParam String email) {
        customerService.resendVerificationEmail(email);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Email xác thực đã được gửi lại thành công.")
                .build();
        return ResponseEntity.ok(response);
    }

    // Thêm method này vào CustomerController:

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable int id) {
        CustomerDto customerDto = customerService.getCustomerById(id);
        ApiResponse<CustomerDto> response = ApiResponse.<CustomerDto>builder()
                .code(HttpStatus.OK.value())
                .message("Customer retrieved successfully")
                .data(customerDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByPhone(@PathVariable String phone) {
        CustomerDto customerDto = customerService.getCustomerByPhone(phone);
        ApiResponse<CustomerDto> response = ApiResponse.<CustomerDto>builder()
                .code(HttpStatus.OK.value())
                .message("Customer retrieved successfully")
                .data(customerDto)
                .build();
        return ResponseEntity.ok(response);
    }

    // cho phép lấy tất cả khách hàng (không theo store)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        ApiResponse<List<CustomerDto>> response = ApiResponse.<List<CustomerDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Customers retrieved successfully")
                .data(customers)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(@PathVariable int customerId, @RequestBody CustomerDto customerDto) {
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerDto);
        ApiResponse<CustomerDto> response = ApiResponse.<CustomerDto>builder()
                .code(HttpStatus.OK.value())
                .message("Customer updated successfully")
                .data(updatedCustomer)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable int customerId) {
        customerService.deleteCustomer(customerId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Customer deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/check-token/{token}")
    // public ResponseEntity<ApiResponse<Boolean>> checkToken(@PathVariable String token) {
    //     boolean exists = customerService.checkTokenExists(token);
    //     ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
    //             .code(HttpStatus.OK.value())
    //             .message(exists ? "Token hợp lệ" : "Token không tồn tại hoặc đã hết hạn")
    //             .data(exists)
    //             .build();
    //     return ResponseEntity.ok(response);
    // }
}
