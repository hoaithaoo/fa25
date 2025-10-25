package swp391.fa25.saleElectricVehicle;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SaleElectricVehicleApplication {

	public static void main(String[] args) {
        SpringApplication.run(SaleElectricVehicleApplication.class, args);
	}

    // Set default timezone to Asia/Ho_Chi_Minh
    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }
}
