package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.request.customer.CreateCustomerRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(
            @Valid @RequestBody CustomerDto request) {
        CustomerDto createdCustomer = customerService.createCustomer(request);
        ApiResponse<CustomerDto> response = ApiResponse.<CustomerDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Customer created successfully")
                .data(createdCustomer)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
}
