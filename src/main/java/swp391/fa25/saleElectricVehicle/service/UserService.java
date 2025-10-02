package swp391.fa25.saleElectricVehicle.service;


import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.payload.request.IntrospectRequest;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.response.IntrospectResponse;
import swp391.fa25.saleElectricVehicle.payload.response.LoginResponse;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto findUserById(int userId);
    List<UserDto> findAllUsers();
    UserDto updateUser(int userId, UserDto userDto);
    void deleteUser(int userId);

    LoginResponse login(LoginRequest loginRequest);
    IntrospectResponse introspect(IntrospectRequest introspectRequest);
}
