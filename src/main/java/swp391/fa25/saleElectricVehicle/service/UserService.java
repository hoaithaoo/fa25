package swp391.fa25.saleElectricVehicle.service;


import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.payload.request.user.CreateUserRequest;
import swp391.fa25.saleElectricVehicle.payload.request.IntrospectRequest;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.request.user.UpdateUserProfileRequest;
import swp391.fa25.saleElectricVehicle.payload.response.*;
import swp391.fa25.saleElectricVehicle.payload.response.user.CreateUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.GetUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.UpdateUserProfileResponse;

import java.util.List;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest userRequest);
    UserDto getUserById(int userId);
    User getUserEntityById(int userId);
    List<GetUserResponse> getUserByName(String name);
    List<GetUserResponse> getAllUsers();
    User getCurrentUserEntity();
    User getUserByEmail(String email);
//    UserDto updateOwnProfile(int userId, UserDto userDto);
    UpdateUserProfileResponse updateUserProfile(int userId, UpdateUserProfileRequest updateUserProfileRequest);
    void deleteUser(int userId);

//    LoginResponse login(LoginRequest loginRequest);
//    IntrospectResponse introspect(IntrospectRequest introspectRequest);
}
