package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto);
//    CustomerDto getCustomerById(int customerId);        // ← THÊM dòng này
    CustomerDto getCustomerByPhone(String phone);
    List<CustomerDto> getAllCustomers();
    List<CustomerDto> getAllCustomersByStaffId(int staffId);
    CustomerDto updateCustomer(int customerId, CustomerDto customerDto);
    void deleteCustomer(int customerId);
}
