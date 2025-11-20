package swp391.fa25.saleElectricVehicle.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import swp391.fa25.saleElectricVehicle.payload.request.IntrospectRequest;
import swp391.fa25.saleElectricVehicle.service.LoginService;
import swp391.fa25.saleElectricVehicle.service.TokenBlacklistService;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${app.jwt-secret}")
    private String signerKey;

    @Autowired
    LoginService loginService;
    
    @Autowired
    TokenBlacklistService tokenBlacklistService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;


    @Override
    public Jwt decode(String token) throws JwtException {
        
        // Kiểm tra token có trong blacklist không
        if (tokenBlacklistService.isBlacklisted(token)) {
            throw new BadCredentialsException("Token has been revoked");
        }

        var response = loginService.introspect(
                IntrospectRequest.builder().token(token).build());

        if (!response.isValid()) throw new BadCredentialsException("Token invalid or expired"); // Ném BadCredentialsException;

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
