package swp391.fa25.saleElectricVehicle.service;

import jakarta.mail.MessagingException;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;

public interface AuthenService {
    Jwt.TokenInfor generateAccessTokenFromRefreshToken(String refreshToken);
//    void createAndSendVerificationLink(String email) throws MessagingException;
    String verifyLink(String token);
}
