package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;
import swp391.fa25.saleElectricVehicle.payload.request.ChangePasswordRequest;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.request.RefreshTokenRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.ChangePasswordResponse;
import swp391.fa25.saleElectricVehicle.payload.response.LoginResponse;
import swp391.fa25.saleElectricVehicle.service.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    LoginService loginService;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    UserService userService;
    
    @Autowired
    TokenBlacklistService tokenBlacklistService;
    
    @Value("${app.jwt-access-expiration-milliseconds}")
    private long accessTokenExpiration;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login (@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = loginService.login(loginRequest);

        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .code(HttpStatus.OK.value())
                .message("User login successfully")
                .data(loginResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            Jwt.TokenInfor newAccessToken = authTokenService.generateAccessTokenFromRefreshToken(request.getRefreshToken());
            
            ApiResponse<Jwt.TokenInfor> response = ApiResponse.<Jwt.TokenInfor>builder()
                    .code(HttpStatus.OK.value())
                    .message("Token refreshed successfully")
                    .data(newAccessToken)
                    .build();
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AppException e) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid or expired refresh token")
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Authorization header is missing or invalid")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Extract token từ header
        String token = authHeader.substring(7); // Bỏ "Bearer "
        
        try {
            // Thêm token vào blacklist với TTL = thời gian hết hạn của access token
            tokenBlacklistService.addToBlacklist(token, accessTokenExpiration);
            
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("Logout successfully")
                    .build();
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error during logout")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse changePasswordResponse = userService.changePassword(changePasswordRequest);
        
        ApiResponse<ChangePasswordResponse> response = ApiResponse.<ChangePasswordResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Đổi mật khẩu thành công")
                .data(changePasswordResponse)
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
