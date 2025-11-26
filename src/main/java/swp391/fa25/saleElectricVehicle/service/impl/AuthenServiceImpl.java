package swp391.fa25.saleElectricVehicle.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.service.AuthenService;
import swp391.fa25.saleElectricVehicle.service.CustomerService;
import swp391.fa25.saleElectricVehicle.service.EmailService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.text.ParseException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenServiceImpl implements AuthenService {

    @Autowired
    Jwt jwt;

    @Autowired
    UserService userService;

    @Autowired
    private CustomerService customerService;

//    @Autowired
//    StringRedisTemplate stringRedisTemplate;
//
//    @Autowired
//    private EmailService emailService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    //generate access token from refresh token
    @Override
    public Jwt.TokenInfor generateAccessTokenFromRefreshToken(String refreshToken) {
        try {
            // Verify refresh token
            SignedJWT signedJWT = jwt.verifyToken(refreshToken);

            // Extract userId from refresh token
            Integer userId = signedJWT.getJWTClaimsSet().getIntegerClaim("userId");

            if (userId == null) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            UserDto userDto = userService.getUserById(userId);

            // Tạo access token mới
            return jwt.generateAccessTokenInfor(userDto);

        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

//    // Thay đổi tên hàm để phản ánh chức năng mới
//    @Override
//    public void createAndSendVerificationLink(String email) throws MessagingException {
//        Customer customer = customerService.getCustomerByEmail(email);
//
////        String tokenKey = "VERIFY_TOKEN:" + email;
//        String timeKey = "VERIFY_TIME:" + email; // Vẫn giữ cơ chế kiểm tra thời gian gửi
//
//        // 1. Kiểm tra thời gian gửi gần nhất (vẫn giữ nguyên logic này)
//        String lastSentTimeStr = stringRedisTemplate.opsForValue().get(timeKey);
//        if (lastSentTimeStr != null) {
//            long lastSentTime = Long.parseLong(lastSentTimeStr);
//            long now = System.currentTimeMillis();
//            if (now - lastSentTime < 30_000) { // 30 giây
//                throw new AppException(ErrorCode.VERIFICATION_ALREADY_SENT_RECENTLY);
//            }
//        }
//
//        // 2. Tạo Token Xác minh mới (Dài và phức tạp hơn OTP)
//        String verificationToken = generateVerificationToken(); // Hàm mới
//
//        // 3. Xây dựng URL xác nhận
//        // QUAN TRỌNG: Đây là URL API Endpoint của BACKEND mà khách hàng sẽ click vào.
//        // Dùng http://localhost:8080/verify để test với Swagger/Postman
//        String verificationUrl = "http://localhost:8080/verify?token=" + verificationToken;
////        String verificationUrl = "http://localhost:5173/verify?token=" + verificationToken;
//
//        // 4. LƯU TRỮ ĐỒNG BỘ MỚI: Key = Token, Value = Email
//        // Lưu Token (Key) và Email (Value) vào Redis
//        stringRedisTemplate.opsForValue().set(verificationToken, email, 60, TimeUnit.MINUTES);
//
//        // Lưu thời gian gửi (Key = timeKey, Value = timestamp)
//        stringRedisTemplate.opsForValue().set(timeKey, String.valueOf(System.currentTimeMillis()), 60, TimeUnit.MINUTES);
//
//        System.out.println("🔹 Verification Token stored in Redis: Key = " + verificationToken + ", Value = " + email);
//
//        // 5. Gửi email chứa Link xác nhận
//        // Cần cập nhật hàm emailService để gửi link thay vì mã OTP
//        emailService.sendVerificationLink(email, verificationUrl, customer.getFullName());
//    }
//
//    /** Hàm mới: Tạo token độc nhất, ví dụ sử dụng UUID */
//    public String generateVerificationToken() {
//        return UUID.randomUUID().toString();
//    }

    // Đăng ký Endpoint này trong Controller của bạn để nhận request khi click link
// Ví dụ: @GetMapping("/verify")
    @Override
    public String verifyLink(String token) {
        // Lấy CustomerDto từ Redis và lưu vào DB
        CustomerDto customerDto = customerService.saveCustomerFromRedis(token);

        // CHUYỂN HƯỚNG ĐẾN TRANG THÔNG BÁO THÀNH CÔNG
        // Địa chỉ của một trang tĩnh trên Frontend/Website thông báo rằng xác minh thành công
//        String successUrl = backendUrl + "/verify-email-success?email=" + customerDto.getEmail();
        String successUrl = frontendUrl + "/verify-email-success?email=" + customerDto.getEmail();
        // Hoặc có thể redirect về trang login/register

        return successUrl;
    }
}
