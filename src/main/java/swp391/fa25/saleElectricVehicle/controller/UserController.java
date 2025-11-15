package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.entity.User;
import jakarta.validation.Valid;
import swp391.fa25.saleElectricVehicle.payload.request.user.CreateUserRequest;
import swp391.fa25.saleElectricVehicle.payload.request.user.UpdateOwnProfileUserRequest;
import swp391.fa25.saleElectricVehicle.payload.request.user.UpdateUserProfileRequest;
import swp391.fa25.saleElectricVehicle.payload.request.user.UpdateUserStatusRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.CreateUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.GetUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.UpdateUserProfileResponse;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
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
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<List<GetUserResponse>>> getUserById(@PathVariable String name) {
        List<GetUserResponse> user = userService.getUserByName(name);
        ApiResponse<List<GetUserResponse>> response = ApiResponse.<List<GetUserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("User fetched successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GetUserResponse>>> getAllUsers() {
        List<GetUserResponse> users = userService.getAllUsers();
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

    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateUserStatus
            (@PathVariable int userId, @Valid @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {
        UpdateUserProfileResponse updatedUser = userService.updateUserStatus(userId, updateUserStatusRequest);

        ApiResponse<UpdateUserProfileResponse> response = ApiResponse.<UpdateUserProfileResponse>builder()
                .code(HttpStatus.OK.value())
                .message("User status updated successfully")
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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetUserResponse>> getCurrentUser() {
        User currentUser = userService.getCurrentUserEntity();
        GetUserResponse userResponse = GetUserResponse.builder()
                .userId(currentUser.getUserId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .phone(currentUser.getPhone())
                .status(currentUser.getStatus().name())
                .storeId(currentUser.getStore() != null ? currentUser.getStore().getStoreId() : 0)
                .storeName(currentUser.getStore() != null ? currentUser.getStore().getStoreName() : null)
                .roleId(currentUser.getRole().getRoleId())
                .roleName(currentUser.getRole().getRoleName())
                .createdAt(currentUser.getCreatedAt())
                .build();
        
        ApiResponse<GetUserResponse> response = ApiResponse.<GetUserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("User information fetched successfully")
                .data(userResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateOwnProfile
            (@RequestBody UpdateOwnProfileUserRequest updateOwnProfileRequest) {
        UpdateUserProfileResponse updatedUser = userService.updateOwnProfile(updateOwnProfileRequest);

        ApiResponse<UpdateUserProfileResponse> response = ApiResponse.<UpdateUserProfileResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .data(updatedUser)
                .build();
        return ResponseEntity.ok(response);
    }
}
