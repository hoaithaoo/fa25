package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.repository.CustomerRepository;
import swp391.fa25.saleElectricVehicle.service.CustomerService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        if (customerRepository.findCustomerByPhone(customerDto.getPhone()) != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (customerRepository.existsCustomerByEmail(customerDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        Customer newCustomer = Customer.builder()
                .fullName(customerDto.getFullName())
                .address(customerDto.getAddress())
                .email(customerDto.getEmail())
                .phone(customerDto.getPhone())
                .createdAt(LocalDateTime.now())
                .build();

        customerRepository.save(newCustomer);

        return mapToDto(newCustomer);
    }

    @Override
    public CustomerDto getCustomerById(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST); // Nên tạo CUSTOMER_NOT_EXIST
        }

        return mapToDto(customer);
    }

    @Override
    public CustomerDto getCustomerByPhone(String phone) {
        Customer customer = customerRepository.findCustomerByPhone(phone);

        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        return mapToDto(customer);
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<CustomerDto> getAllCustomersByStaffId(int staffId) {
        List<Customer> customers = customerRepository.findCustomersByStaffId(staffId);
        return customers.stream().map(this::mapToDto).toList();
    }

    @Override
    public CustomerDto updateCustomer(int customerId, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

//        if (customerRepository.findCustomerByPhone(customerDto.getPhone()) != null) {
        if (!customer.getPhone().equals(customerDto.getPhone()) &&
                customerRepository.findCustomerByPhone(customerDto.getPhone()) != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

//        if (customerRepository.existsCustomerByEmail(customerDto.getEmail())) {
//            throw new AppException(ErrorCode.EMAIL_EXISTED);
//        }
        // ✅ SỬA LẠI:
        if (!customer.getEmail().equals(customerDto.getEmail()) &&
                customerRepository.existsCustomerByEmail(customerDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (customerDto.getEmail() != null && !customerDto.getEmail().trim().isEmpty()) {
            customer.setEmail(customerDto.getEmail());
        }

        if (customerDto.getFullName() != null && !customerDto.getFullName().trim().isEmpty()) {
            customer.setFullName(customerDto.getFullName());
        }

        if (customerDto.getAddress() != null && !customerDto.getAddress().trim().isEmpty()) {
            customer.setAddress(customerDto.getAddress());
        }

        if (customerDto.getPhone() != null && !customerDto.getPhone().trim().isEmpty()) {
            customer.setPhone(customerDto.getPhone());
        }

        customer.setUpdatedAt(LocalDateTime.now());

        customerRepository.save(customer);

        return mapToDto(customer);
    }

    @Override
    public void deleteCustomer(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        customerRepository.delete(customer);
    }

    private CustomerDto mapToDto(Customer customer) {
        return CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .fullName(customer.getFullName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }
}
