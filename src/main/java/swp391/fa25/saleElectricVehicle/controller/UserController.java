package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Thêm để Frontend có thể call API
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(createdUser)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response); //return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable int userId) {
        UserDto user = userService.findUserById(userId);
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .code(HttpStatus.OK.value())
                .message("User fetched successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.findAllUsers();
        ApiResponse<List<UserDto>> response = ApiResponse.<List<UserDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Users fetched successfully")
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable int userId, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateOwnProfile(userId, userDto);

        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .code(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(updatedUser)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("User deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
