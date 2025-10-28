package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.request.customer.CreateCustomerRequest;

import java.util.List;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto request); // ✅ Đổi từ CustomerDto → CreateCustomerRequest
    CustomerDto getCustomerById(int customerId);
    Customer getCustomerEntityById(int customerId);
    CustomerDto getCustomerByPhone(String phone);
    List<CustomerDto> getAllCustomers();
    List<CustomerDto> getAllCustomersByStaffId(int staffId);
    CustomerDto updateCustomer(int customerId, CustomerDto customerDto);
    void deleteCustomer(int customerId);
}


