package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.request.user.CreateUserRequest;
import swp391.fa25.saleElectricVehicle.payload.request.user.UpdateUserProfileRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.CreateUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.GetUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.UpdateUserProfileResponse;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(@RequestBody CreateUserRequest userRequest) {
        CreateUserResponse createdUser = userService.createUser(userRequest);
        ApiResponse<CreateUserResponse> response = ApiResponse.<CreateUserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(createdUser)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<List<GetUserResponse>>> getUserById(@PathVariable String name) {
        List<GetUserResponse> user = userService.findUserByName(name);
        ApiResponse<List<GetUserResponse>> response = ApiResponse.<List<GetUserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("User fetched successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GetUserResponse>>> getAllUsers() {
        List<GetUserResponse> users = userService.findAllUsers();
        ApiResponse<List<GetUserResponse>> response = ApiResponse.<List<GetUserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Users fetched successfully")
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateUserProfile
            (@PathVariable int userId, @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {
        UpdateUserProfileResponse updatedUser = userService.updateUserProfile(userId, updateUserProfileRequest);

        ApiResponse<UpdateUserProfileResponse> response = ApiResponse.<UpdateUserProfileResponse>builder()
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
