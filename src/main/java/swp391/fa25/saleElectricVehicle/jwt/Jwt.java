package swp391.fa25.saleElectricVehicle.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.service.UserService;


import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Component
@Getter
public class Jwt {

    @Value("${app.jwt-secret}")
    private String secretKey;

    @Value("${app.jwt-access-expiration-milliseconds}")
    private long accessExpiration;

    @Value("${app.jwt-refresh-expiration-milliseconds}")
    private long refreshExpiration;

    //tao record de luu thong tin token
    public record TokenInfor(String token, Date expiryDate){}
    public record TokenPair(TokenInfor accessToken, TokenInfor refreshToken){}

    //method tao access token va refresh token
    public TokenPair generateTokenPair(UserDto userDto) {
        TokenInfor accessToken = generateTokenInfor(userDto, accessExpiration, "accessToken");
        TokenInfor refreshToken = generateTokenInfor(userDto, refreshExpiration, "refreshToken");
        return new TokenPair(accessToken, refreshToken);
    }

    private TokenInfor generateTokenInfor(UserDto userDto, long expiration, String tokenType) {
        try {
            //tao header
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            //tinh thoi gian het han
            Date issueTime = new Date();
            Date expiryTime = new Date(issueTime.getTime() + expiration);

            //tao claims (payload)
            JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder()
                    .issuer("group3.com")
                    .issueTime(issueTime)
                    .expirationTime(expiryTime)
                    .jwtID(UUID.randomUUID().toString());

            if (tokenType.equals("accessToken")) {
                claimsSet.subject(userDto.getEmail())
                        .claim("roleName", userDto.getRoleId());
            } else {
                claimsSet.claim("userId", userDto.getUserId());
            }

            //gop header va claims
            SignedJWT signedJWT = new SignedJWT(header, claimsSet.build());
            //dung MACSinger de tao chu ky
            signedJWT.sign(new MACSigner(secretKey.getBytes()));

            //chuyen thanh dinh dang header.payload.signature
            TokenInfor tokenInfor = new TokenInfor(signedJWT.serialize(), expiryTime);
            return tokenInfor;
        } catch (JOSEException e) {
            throw new RuntimeException("Error creating JWT token", e);
        }
    }

    public TokenInfor generateAccessTokenInfor(UserDto userDto) {
        return generateTokenInfor(userDto, accessExpiration, "accessToken");
    }

    //verify token
        public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean verified = signedJWT.verify(verifier);

            if (!(verified && expiryTime.after(new Date()))) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            return signedJWT;
        }

        public Integer extractUserId(String token) {
            try {
                SignedJWT signedJWT = SignedJWT.parse(token); // Parse token
                return signedJWT.getJWTClaimsSet().getIntegerClaim("userId"); // Lấy userId
            } catch (ParseException e) {
                throw new RuntimeException("Invalid token format", e);
            }
        }

    }
