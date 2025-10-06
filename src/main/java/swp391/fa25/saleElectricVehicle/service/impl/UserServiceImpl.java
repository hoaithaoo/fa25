package swp391.fa25.saleElectricVehicle.service.impl;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
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
import swp391.fa25.saleElectricVehicle.repository.RoleRepository;
import swp391.fa25.saleElectricVehicle.repository.StoreRepository;
import swp391.fa25.saleElectricVehicle.repository.UserRepository;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.text.ParseException;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Jwt jwt;

    @Override
    public CreateUserResponse createUser(CreateUserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByPhone(userRequest.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        Role role = roleRepository.findById(userRequest.getRoleId()).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }

        Store store = storeRepository.findById(userRequest.getStoreId()).orElse(null);
        if (store == null && (userRequest.getRoleId() != 1 || userRequest.getRoleId() != 2)) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        User newUser = User.builder()
                .fullName(userRequest.getFullName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .status(UserStatus.PENDING)
                .store(store)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);
        return CreateUserResponse.builder()
                .userId(newUser.getUserId())
                .fullName(newUser.getFullName())
                .email(newUser.getEmail())
                .phone(newUser.getPhone())
                .status(newUser.getStatus())
                .storeId(newUser.getStore().getStoreId())
                .roleId(newUser.getRole().getRoleId())
                .build();
    }

    @Override
    public UserDto findUserById(int userId) {
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
    public List<GetUserResponse> findUserByName(String name) {
        List<User> user = userRepository.findUsersByFullNameContaining(name);

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        return user.stream().map(u -> GetUserResponse.builder()
                .userId(u.getUserId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .status(u.getStatus())
                .storeId(u.getStore().getStoreId())
                .roleId(u.getRole().getRoleId())
                .build()).toList();
    }

    @Override
    public List<GetUserResponse> findAllUsers() {
        return userRepository.findAll().stream().map(u -> GetUserResponse.builder()
                .userId(u.getUserId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .status(u.getStatus())
                .storeId(u.getStore().getStoreId())
                .roleId(u.getRole().getRoleId())
                .build()).toList();
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
            Role role = roleRepository.findById(updateUserProfileRequest.getRoleId()).orElse(null);
            if (role == null) {
                throw new AppException(ErrorCode.ROLE_NOT_EXIST);
            }
        }

        if (updateUserProfileRequest.getStoreId() != 0) {
            Store store = storeRepository.findById(updateUserProfileRequest.getStoreId()).orElse(null);
            if (store == null && (updateUserProfileRequest.getRoleId() != 1 || updateUserProfileRequest.getRoleId() != 2)) {
                throw new AppException(ErrorCode.STORE_NOT_EXIST);
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
                .storeId(user.getStore().getStoreId())
                .roleId(user.getRole().getRoleId())
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

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .roleId(user.getRole().getRoleId())
                .build();

        Jwt.TokenPair tokenPair = jwt.generateTokenPair(userDto);

        return LoginResponse.builder()
                .accessToken(tokenPair.accessToken().token())
                .refreshToken(tokenPair.refreshToken().token())
                .accessTokenExpiry(tokenPair.accessToken().expiryDate().getTime())
                .refreshTokenExpiry(tokenPair.refreshToken().expiryDate().getTime())
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        var token = introspectRequest.getToken();
        boolean isValid = true;

        try {
            // Kiểm tra tính hợp lệ của token
            jwt.verifyToken(token);
        } catch (AppException e) {
            // Xử lý lỗi AppException
            isValid = false;
        } catch (JOSEException e) {
            // Xử lý lỗi JOSEException
            isValid = false;
        } catch (ParseException e) {
            // Xử lý lỗi ParseException
            isValid = false;
        } catch (Exception e) {
            // Bắt tất cả các lỗi không xác định
            isValid = false;
        }

        // Trả về IntrospectResponse với trạng thái valid và thông báo lỗi nếu có
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }
}
