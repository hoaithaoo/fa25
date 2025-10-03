package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.jwt.Jwt;

public interface AuthTokenService {
    Jwt.TokenInfor generateAccessTokenFromRefreshToken(String refreshToken);
}
