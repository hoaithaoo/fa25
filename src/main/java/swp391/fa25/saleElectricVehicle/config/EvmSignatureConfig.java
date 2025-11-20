package swp391.fa25.saleElectricVehicle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class EvmSignatureConfig {
    
    @Value("${evm.default.signature.url:}")
    private String defaultSignatureUrl;
}

