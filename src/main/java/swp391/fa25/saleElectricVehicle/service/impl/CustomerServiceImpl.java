package swp391.fa25.saleElectricVehicle.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.repository.CustomerRepository;
import swp391.fa25.saleElectricVehicle.service.CustomerService;
import swp391.fa25.saleElectricVehicle.service.EmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot tự config sẵn với JavaTimeModule

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private StoreService storeService;

    @Override
    public String createCustomer(CustomerDto customerDto) {
        // Kiểm tra phone đã tồn tại trong DB chưa
        if (customerRepository.findCustomerByPhone(customerDto.getPhone()) != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Kiểm tra email đã tồn tại trong DB chưa
        if (customerRepository.existsCustomerByEmail(customerDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Kiểm tra identificationNumber đã tồn tại trong DB chưa
        if (customerDto.getIdentificationNumber() != null 
                && !customerDto.getIdentificationNumber().trim().isEmpty()
                && customerRepository.existsCustomerByIdentificationNumber(customerDto.getIdentificationNumber())) {
            throw new AppException(ErrorCode.IDENTIFICATION_NUMBER_EXISTED);
        }

        // Tạo verification token
        String verificationToken = UUID.randomUUID().toString();
        String redisKey = "CUSTOMER_VERIFY:" + verificationToken;

        // Set createdAt
        customerDto.setCreatedAt(LocalDateTime.now());

        try {
            // Serialize CustomerDto thành JSON string
            String customerJson = objectMapper.writeValueAsString(customerDto);
            
            // Lưu CustomerDto (dạng JSON string) vào Redis với TTL 60 phút
            stringRedisTemplate.opsForValue().set(redisKey, customerJson, 60, TimeUnit.MINUTES);
            
            // Lưu thêm mapping email -> token để có thể tìm lại sau
            String emailKey = "CUSTOMER_EMAIL:" + customerDto.getEmail();
            stringRedisTemplate.opsForValue().set(emailKey, verificationToken, 60, TimeUnit.MINUTES);
            
            // Tạo verification URL - backend endpoint để nhận callback
            // Sử dụng app.backend-url từ application.properties (có thể là ngrok URL hoặc domain thật)
//            String verificationUrl = backendUrl + contextPath + "/auth/verify-email?token=" + verificationToken;
            String verificationUrl = frontendUrl + contextPath + "/auth/verify-email?token=" + verificationToken;


            // Gửi email verification cho khách hàng
            emailService.sendVerificationLink(customerDto.getEmail(), verificationUrl, customerDto.getFullName());

            return verificationToken;
        } catch (Exception e) {
            // Nếu gửi email thất bại, xóa khỏi Redis
            stringRedisTemplate.delete(redisKey);
            stringRedisTemplate.delete("CUSTOMER_EMAIL:" + customerDto.getEmail());
            if (e instanceof MessagingException) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // Lấy từ Redis và lưu vào DB (khi nhận callback verify)
    @Override
    public CustomerDto saveCustomerFromRedis(String token) {
        String redisKey = "CUSTOMER_VERIFY:" + token;
        
        // Lấy JSON string từ Redis
        String customerJson = stringRedisTemplate.opsForValue().get(redisKey);
        if (customerJson == null) {
            // // Debug: Kiểm tra xem Redis có đang chạy không
            // try {
            //     stringRedisTemplate.opsForValue().set("TEST_KEY", "test", 1, TimeUnit.SECONDS);
            //     System.out.println("✅ Redis đang chạy");
            // } catch (Exception e) {
            //     System.out.println("❌ Redis không kết nối được: " + e.getMessage());
            // }
            
            // System.out.println("❌ Token không tồn tại trong Redis");
            // System.out.println("❌ Token: " + token);
            // System.out.println("❌ Redis Key: " + redisKey);
            
            // // Kiểm tra xem có phải token đã được sử dụng (đã verify) không
            // // Bằng cách check xem email đã tồn tại trong DB chưa
            // // (Nhưng không thể biết email từ token nếu không có trong Redis)
            
            throw new AppException(ErrorCode.INVALID_VERIFICATION_LINK);
        }
        
        // System.out.println("✅ Tìm thấy token trong Redis: " + token);

        CustomerDto customerDto;
        try {
            // Deserialize JSON string thành CustomerDto
            customerDto = objectMapper.readValue(customerJson, CustomerDto.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_LINK);
        }

        // Kiểm tra lại phone và email trước khi lưu (phòng trường hợp đã có người khác tạo)
        if (customerRepository.findCustomerByPhone(customerDto.getPhone()) != null) {
            stringRedisTemplate.delete(redisKey); // Xóa khỏi Redis
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (customerRepository.existsCustomerByEmail(customerDto.getEmail())) {
            stringRedisTemplate.delete(redisKey); // Xóa khỏi Redis
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Tạo Customer entity và lưu vào DB
        // Lưu ý: Customer chỉ được lưu vào DB sau khi verify email, nên không cần field isEmailVerified
        Customer newCustomer = Customer.builder()
                .fullName(customerDto.getFullName())
                .address(customerDto.getAddress())
                .email(customerDto.getEmail())
                .phone(customerDto.getPhone())
                .identificationNumber(customerDto.getIdentificationNumber())
                .createdAt(customerDto.getCreatedAt() != null ? customerDto.getCreatedAt() : LocalDateTime.now())
                .build();

        customerRepository.save(newCustomer);

        // Xóa khỏi Redis sau khi lưu thành công
        stringRedisTemplate.delete(redisKey);
        stringRedisTemplate.delete("CUSTOMER_EMAIL:" + customerDto.getEmail());

        return mapToDto(newCustomer);
    }

    // Gửi lại email verification cho customer
    @Override
    public void resendVerificationEmail(String email) {
        // Kiểm tra customer đã tồn tại trong DB chưa
        if (customerRepository.existsCustomerByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Tìm token trong Redis từ email
        String emailKey = "CUSTOMER_EMAIL:" + email;
        String oldToken = stringRedisTemplate.opsForValue().get(emailKey);
        
        if (oldToken == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        String oldRedisKey = "CUSTOMER_VERIFY:" + oldToken;
        
        // Lấy customer JSON từ Redis
        String customerJson = stringRedisTemplate.opsForValue().get(oldRedisKey);
        if (customerJson == null) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_LINK);
        }

        CustomerDto customerDto;
        try {
            // Deserialize JSON string thành CustomerDto
            customerDto = objectMapper.readValue(customerJson, CustomerDto.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_LINK);
        }

        // Tạo token mới
        String newVerificationToken = UUID.randomUUID().toString();
        String newRedisKey = "CUSTOMER_VERIFY:" + newVerificationToken;

        try {
            // Xóa token cũ và lưu token mới
            stringRedisTemplate.delete(oldRedisKey);
            stringRedisTemplate.delete(emailKey);
            
            // Lưu lại với token mới
            stringRedisTemplate.opsForValue().set(newRedisKey, customerJson, 60, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(emailKey, newVerificationToken, 60, TimeUnit.MINUTES);
            
            // Tạo verification URL
//            String verificationUrl = backendUrl + contextPath + "/auth/verify-email?token=" + newVerificationToken;
            String verificationUrl = frontendUrl + contextPath + "/auth/verify-email?token=" + newVerificationToken;

            // Gửi lại email verification
            emailService.sendVerificationLink(email, verificationUrl, customerDto.getFullName());
        } catch (MessagingException e) {
            // Nếu gửi email thất bại, xóa khỏi Redis
            stringRedisTemplate.delete(newRedisKey);
            stringRedisTemplate.delete(emailKey);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public Customer getCustomerEntityById(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        return customer;
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
    public Customer getCustomerByEmail(String email) {
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        return customer;
    }

    @Override
    public CustomerDto updateCustomer(int customerId, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

//        if (customerRepository.findCustomerByPhone(customerDto.getPhone()) != null) {
        if (customerDto.getPhone() != null
                && !customerDto.getPhone().trim().isEmpty()) {
            if (!customer.getPhone().equals(customerDto.getPhone())
                    && customerRepository.findCustomerByPhone(customerDto.getPhone()) != null) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            customer.setPhone(customerDto.getPhone());
        }

//        if (customerRepository.existsCustomerByEmail(customerDto.getEmail())) {
//            throw new AppException(ErrorCode.EMAIL_EXISTED);
//        }
        // ✅ SỬA LẠI:
        if (customerDto.getEmail() != null
                && !customerDto.getEmail().trim().isEmpty()
                && !customer.getEmail().equals(customerDto.getEmail()) &&
                customerRepository.existsCustomerByEmail(customerDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if(customerDto.getIdentificationNumber() != null && !customerDto.getIdentificationNumber().trim().isEmpty()) {
            if (!customer.getIdentificationNumber().equals(customerDto.getIdentificationNumber())
                    && customerRepository.existsCustomerByIdentificationNumber(customerDto.getIdentificationNumber())) {
                throw new AppException(ErrorCode.IDENTIFICATION_NUMBER_EXISTED);

            }
            customer.setIdentificationNumber(customerDto.getIdentificationNumber());
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

    // @Override
    // public boolean checkTokenExists(String token) {
    //     String redisKey = "CUSTOMER_VERIFY:" + token;
    //     String customerJson = stringRedisTemplate.opsForValue().get(redisKey);
    //     return customerJson != null;
    // }

    private CustomerDto mapToDto(Customer customer) {
//        User user = userService.getCurrentUserEntity();
//        Store store = storeService.getCurrentStoreEntity(user.getUserId());
        return CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .fullName(customer.getFullName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .identificationNumber(customer.getIdentificationNumber())
                .createdAt(customer.getCreatedAt())
//                .storeId(store.getStoreId())
//                .storeName(store.getStoreName())
                .build();
    }
}
