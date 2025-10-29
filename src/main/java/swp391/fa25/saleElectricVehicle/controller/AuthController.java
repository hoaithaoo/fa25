package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.request.RefreshTokenRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.LoginResponse;
import swp391.fa25.saleElectricVehicle.service.AuthTokenService;
import swp391.fa25.saleElectricVehicle.service.LoginService;
import swp391.fa25.saleElectricVehicle.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    LoginService loginService;

    @Autowired
    AuthTokenService authTokenService;

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
            return ResponseEntity.ok(newAccessToken);
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid refresh token");
        }
    }
}
