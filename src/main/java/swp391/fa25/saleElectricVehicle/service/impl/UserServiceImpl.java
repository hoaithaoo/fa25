package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Role;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.repository.RoleRepository;
import swp391.fa25.saleElectricVehicle.repository.StoreRepository;
import swp391.fa25.saleElectricVehicle.repository.UserRepository;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        Store store = storeRepository.findById(userDto.getStoreId()).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.NOT_EXIST);
        }

        Role role = roleRepository.findById(userDto.getRoleId()).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.NOT_EXIST);
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User newUser = User.builder()
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .password(userDto.getPassword())
                .isActive(userDto.getIsActive())
                .store(store)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);
        return userDto;
    }

    @Override
    public UserDto findUserById(int userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new AppException(ErrorCode.NOT_EXIST);
        }

        UserDto userDto = UserDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .storeId(user.getStore().getStoreId())
                .roleId(user.getRole().getRoleId())
                .build();
        return userDto;
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(user -> UserDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .storeId(user.getStore().getStoreId())
                .roleId(user.getRole().getRoleId())
                .build()).toList();
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new AppException(ErrorCode.NOT_EXIST);
        }

        Store store = storeRepository.findById(userDto.getStoreId()).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.NOT_EXIST);
        }

        Role role = roleRepository.findById(userDto.getRoleId()).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.NOT_EXIST);
        }

        User updatedUser = User.builder()
                .userId(userId)
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .isActive(userDto.getIsActive())
                .store(store)
                .role(role)
                .build();

        userRepository.save(updatedUser);

        return userDto;
    }

    @Override
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new AppException(ErrorCode.NOT_EXIST);
        }
        userRepository.delete(user);
    }
}
