package swp391.fa25.saleElectricVehicle.service.impl;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.StoreStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.payload.request.IntrospectRequest;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.response.IntrospectResponse;
import swp391.fa25.saleElectricVehicle.payload.response.LoginResponse;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.UserStatus;
import swp391.fa25.saleElectricVehicle.service.LoginService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.text.ParseException;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Jwt jwt;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        // Kiểm tra nếu user có store và store đó bị inactive thì không cho đăng nhập
        if (user.getStore() != null && user.getStore().getStatus() == StoreStatus.INACTIVE) {
            throw new AppException(ErrorCode.STORE_INACTIVE);
        }

        // Kiểm tra nếu user là staff (không phải admin) và status là PENDING
        boolean isStaff = !user.getRole().getRoleName().equalsIgnoreCase("Quản trị viên");
        boolean requirePasswordChange = isStaff && user.getStatus() == UserStatus.PENDING;

        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .roleId(user.getRole().getRoleId())
                .roleName(user.getRole().getRoleName())
                .build();

        Jwt.TokenPair tokenPair = jwt.generateTokenPair(userDto);

        return LoginResponse.builder()
                .accessToken(tokenPair.accessToken().token())
                .refreshToken(tokenPair.refreshToken().token())
                .accessTokenExpiry(tokenPair.accessToken().expiryDate().getTime())
                .refreshTokenExpiry(tokenPair.refreshToken().expiryDate().getTime())
                .requirePasswordChange(requirePasswordChange)
                .status(user.getStatus().toString())
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        var token = introspectRequest.getToken();
        boolean isValid = true;

        try {
            // Kiểm tra tính hợp lệ của token
            jwt.verifyToken(token);
        } catch (AppException e) {
            // Xử lý lỗi AppException
            isValid = false;
        } catch (JOSEException e) {
            // Xử lý lỗi JOSEException
            isValid = false;
        } catch (ParseException e) {
            // Xử lý lỗi ParseException
            isValid = false;
        } catch (Exception e) {
            // Bắt tất cả các lỗi không xác định
            isValid = false;
        }

        // Trả về IntrospectResponse với trạng thái valid và thông báo lỗi nếu có
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }
}
