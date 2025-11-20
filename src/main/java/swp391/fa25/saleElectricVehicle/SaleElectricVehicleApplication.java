package swp391.fa25.saleElectricVehicle;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import swp391.fa25.saleElectricVehicle.entity.Role;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.UserStatus;
import swp391.fa25.saleElectricVehicle.payload.request.user.CreateUserRequest;
import swp391.fa25.saleElectricVehicle.service.RoleService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class SaleElectricVehicleApplication implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

	public static void main(String[] args) {
        SpringApplication.run(SaleElectricVehicleApplication.class, args);
	}

    // Set default timezone to Asia/Ho_Chi_Minh
    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleService.getAllRoles().isEmpty()) {
            Role role = new Role("Quản trị viên");
            Role role1 = new Role("Nhân viên hãng xe");
            Role role2 = new Role("Quản lý cửa hàng");
            Role role3 = new Role("Nhân viên cửa hàng");
            roleService.createRole(role);
            roleService.createRole(role1);
            roleService.createRole(role2);
            roleService.createRole(role3);
        }

        if (userService.getAllUsers().isEmpty()) {
            User adminUser = new User("ADMIN", "admin@gmail.com", "123", "0356964383",
                    LocalDateTime.now(), roleService.getRoleEntityById(1), UserStatus.PENDING);
            userService.createUser(CreateUserRequest.builder()
                    .fullName(adminUser.getFullName())
                    .email(adminUser.getEmail())
                    .password(adminUser.getPassword())
                    .phone(adminUser.getPhone())
                    .roleId(adminUser.getRole().getRoleId())
                    .build());
        }
    }
}
