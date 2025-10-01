package swp391.fa25.saleElectricVehicle.service;


import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto findUserById(int userId);
    List<UserDto> findAllUsers();
    UserDto updateUser(int userId, UserDto userDto);
    void deleteUser(int userId);
}
