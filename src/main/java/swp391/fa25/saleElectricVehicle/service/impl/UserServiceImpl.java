package swp391.fa25.saleElectricVehicle.service.impl;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Role;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.jwt.Jwt;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.payload.request.IntrospectRequest;
import swp391.fa25.saleElectricVehicle.payload.request.LoginRequest;
import swp391.fa25.saleElectricVehicle.payload.response.IntrospectResponse;
import swp391.fa25.saleElectricVehicle.payload.response.LoginResponse;
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
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findById(userDto.getRoleId()).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }

        Store store = storeRepository.findById(userDto.getStoreId()).orElse(null);
        if (store == null && (userDto.getRoleId() != 1 || userDto.getRoleId() != 2)) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        User newUser = User.builder()
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .password(passwordEncoder.encode(userDto.getPassword()))
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
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
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
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        Store store = storeRepository.findById(userDto.getStoreId()).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        Role role = roleRepository.findById(userDto.getRoleId()).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
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
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
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
