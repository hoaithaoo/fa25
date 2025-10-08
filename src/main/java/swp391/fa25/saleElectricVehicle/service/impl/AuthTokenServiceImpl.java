package swp391.fa25.saleElectricVehicle.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.service.AuthTokenService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.text.ParseException;

@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    @Autowired
    Jwt jwt;

    @Autowired
    UserService userService;

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
}
