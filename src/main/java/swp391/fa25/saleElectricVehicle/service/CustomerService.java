package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    String createCustomer(CustomerDto request); // Nhân viên tạo customer: lưu vào Redis, gửi email, trả về token
    CustomerDto saveCustomerFromRedis(String token); // Lấy từ Redis và lưu vào DB (khi nhận callback verify)
    CustomerDto getCustomerById(int customerId);
    Customer getCustomerEntityById(int customerId);
    CustomerDto getCustomerByPhone(String phone);
    List<CustomerDto> getAllCustomers();

    Customer getCustomerByEmail(String email);
    CustomerDto updateCustomer(int customerId, CustomerDto customerDto);
    void deleteCustomer(int customerId);
    void resendVerificationEmail(String email); // Gửi lại email verification cho customer đã tạo
    // boolean checkTokenExists(String token); // Kiểm tra token có tồn tại trong Redis không
}


