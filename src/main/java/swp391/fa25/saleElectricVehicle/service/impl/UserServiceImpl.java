package swp391.fa25.saleElectricVehicle.service.impl;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Role;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.UserStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.payload.request.user.CreateUserRequest;
import swp391.fa25.saleElectricVehicle.payload.request.IntrospectRequest;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.request.user.UpdateUserProfileRequest;
import swp391.fa25.saleElectricVehicle.payload.response.*;
import swp391.fa25.saleElectricVehicle.payload.response.user.CreateUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.GetUserResponse;
import swp391.fa25.saleElectricVehicle.payload.response.user.UpdateUserProfileResponse;
import swp391.fa25.saleElectricVehicle.repository.UserRepository;
import swp391.fa25.saleElectricVehicle.service.RoleService;
import swp391.fa25.saleElectricVehicle.service.StoreService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StoreService storeService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public CreateUserResponse createUser(CreateUserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByPhone(userRequest.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        Role role = roleService.getRoleEntityById(userRequest.getRoleId());

        Store store = null;
        if (role.getRoleName().equalsIgnoreCase("Quản trị viên") || role.getRoleName().equalsIgnoreCase("Nhân viên hãng xe")) {
            userRequest.setStoreId(0); // Không gán store nếu role là Quản trị viên hoặc Nhân viên hãng xe
        } else {
            store = storeService.getStoreEntityById(userRequest.getStoreId());
        }

        User newUser = userRepository.save(User.builder()
                .fullName(userRequest.getFullName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .status(UserStatus.PENDING)
                .store(store)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build());

        return CreateUserResponse.builder()
                .userId(newUser.getUserId())
                .fullName(newUser.getFullName())
                .email(newUser.getEmail())
                .phone(newUser.getPhone())
                .status(newUser.getStatus().toString())
                .storeId(newUser.getStore() != null ? newUser.getStore().getStoreId() : 0)
                .storeName(newUser.getStore() != null ? newUser.getStore().getStoreName() : null)
                .roleId(newUser.getRole().getRoleId())
                .roleName(newUser.getRole().getRoleName())
                .build();
    }

    @Override
    public User getUserEntityById(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        return user;
    }

    //use at AuthTokenService
    @Override
    public UserDto getUserById(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .roleId(user.getRole().getRoleId())
                .build();
    }

    @Override
    public List<GetUserResponse> getUserByName(String name) {
        List<User> user = userRepository.findUsersByFullNameContaining(name);

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        return user.stream().map(u -> GetUserResponse.builder()
                .userId(u.getUserId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .status(u.getStatus().name())
                .storeId(u.getStore() != null ? u.getStore().getStoreId() : 0)
                .storeName(u.getStore() != null ? u.getStore().getStoreName() : null)
                .roleId(u.getRole().getRoleId())
                .roleName(u.getRole().getRoleName())
                .build()).toList();
    }

    @Override
    public List<GetUserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(u -> GetUserResponse.builder()
                .userId(u.getUserId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .status(u.getStatus().name())
                .storeId(u.getStore() != null ? u.getStore().getStoreId() : 0)
                .storeName(u.getStore() != null ? u.getStore().getStoreName() : null)
                .roleId(u.getRole().getRoleId())
                .roleName(u.getRole().getRoleName())
                .build()).toList();
    }

    // dùng ở tạo order, lấy user hiện tại
    @Override
    public User getCurrentUserEntity() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email);
        if (currentUser == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        return currentUser;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        return user;
    }

//    @Override
//    public UserDto updateOwnProfile(int userId, UserDto userDto) {
//        User user = userRepository.findById(userId).orElse(null);
//        if (user == null) {
//            throw new AppException(ErrorCode.USER_NOT_EXIST);
//        }
//
//        if (userDto.getFullName() != null && !userDto.getFullName().trim().isEmpty()) {
//            user.setFullName(userDto.getFullName());
//        }
//
//        if (userDto.getPhone() != null && !userDto.getPhone().trim().isEmpty()) {
//            user.setPhone(userDto.getPhone());
//        }
//
//        user.setUpdatedAt(LocalDateTime.now());
//
//        userRepository.save(user);
//
//        return userDto;
//    }

    //update profile of any user by Quản trị viên
    @Override
    public UpdateUserProfileResponse updateUserProfile(int userId, UpdateUserProfileRequest updateUserProfileRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        if (updateUserProfileRequest.getEmail() != null && !updateUserProfileRequest.getEmail().trim().isEmpty()) {
            if (!user.getEmail().equals(updateUserProfileRequest.getEmail()) && userRepository.existsByEmail(updateUserProfileRequest.getEmail())) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            user.setEmail(updateUserProfileRequest.getEmail());
        }

        if (updateUserProfileRequest.getPhone() != null && !updateUserProfileRequest.getPhone().trim().isEmpty()) {
            if (!user.getPhone().equals(updateUserProfileRequest.getPhone()) && userRepository.existsByPhone(updateUserProfileRequest.getPhone())) {
                throw new AppException(ErrorCode.PHONE_EXISTED);
            }
            user.setPhone(updateUserProfileRequest.getPhone());
        }

        if (updateUserProfileRequest.getRoleId() != 0) {
            Role role = roleService.getRoleEntityById(updateUserProfileRequest.getRoleId());
            user.setRole(role);
            if (role.getRoleName().equalsIgnoreCase("Quản trị viên") || role.getRoleName().equalsIgnoreCase("Nhân viên hãng xe")) {
                user.setStore(null); // Nếu role là Quản trị viên hoặc Nhân viên hãng xe thì không được gán store
            }
        }

        if (updateUserProfileRequest.getStoreId() != 0) {
            if (!user.getRole().getRoleName().equalsIgnoreCase("Quản trị viên")
                    && !user.getRole().getRoleName().equalsIgnoreCase("Nhân viên hãng xe")) {
                Store store = storeService.getStoreEntityById(updateUserProfileRequest.getStoreId());
                user.setStore(store);
            } else {
                // Nếu role là Quản trị viên hoặc Nhân viên hãng xe thì không được gán store
                throw new AppException(ErrorCode.ROLE_CANNOT_ASSIGN_STORE);
            }
        }

        if (updateUserProfileRequest.getFullName() != null && !updateUserProfileRequest.getFullName().trim().isEmpty()) {
            user.setFullName(updateUserProfileRequest.getFullName());
        }

        if (updateUserProfileRequest.getStatus() != null) {
            user.setStatus(updateUserProfileRequest.getStatus());
        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return UpdateUserProfileResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .storeId(user.getStore() != null ? user.getStore().getStoreId() : 0)
                .storeName(user.getStore() != null ? user.getStore().getStoreName() : null)
                .roleId(user.getRole().getRoleId())
                .roleName(user.getRole().getRoleName())
                .build();
    }

    @Override
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        userRepository.delete(user);
    }
}
