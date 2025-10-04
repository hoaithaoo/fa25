package swp391.fa25.saleElectricVehicle.service;


import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.payload.request.CreateUserRequest;
import swp391.fa25.saleElectricVehicle.payload.request.IntrospectRequest;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.request.UpdateUserProfileRequest;
import swp391.fa25.saleElectricVehicle.payload.response.*;

import java.util.List;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest userRequest);
    UserDto findUserById(int userId);
    List<GetUserResponse> findUserByName(String name);
    List<GetUserResponse> findAllUsers();
//    UserDto updateOwnProfile(int userId, UserDto userDto);
    UpdateUserProfileResponse updateUserProfile(int userId, UpdateUserProfileRequest updateUserProfileRequest);
    void deleteUser(int userId);

    LoginResponse login(LoginRequest loginRequest);
    IntrospectResponse introspect(IntrospectRequest introspectRequest);
}
