package swp391.fa25.saleElectricVehicle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Tắt CSRF (cú pháp mới)
                .csrf(csrf -> csrf.disable())

                // ✅ Cho phép CORS
                .cors(Customizer.withDefaults())

                // ✅ Cho phép truy cập Swagger không cần đăng nhập
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
