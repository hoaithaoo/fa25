package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Role;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.RoleDto;
import swp391.fa25.saleElectricVehicle.repository.RoleRepository;
import swp391.fa25.saleElectricVehicle.service.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        if (roleRepository.existsByRoleName(roleDto.getRoleName())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        Role newRole = Role.builder()
                .roleName(roleDto.getRoleName())
                .build();
        Role savedRole = roleRepository.save(newRole);
        return mapToDto(savedRole);
    }

    // dùng để tìm kiếm role (?)
    @Override
    public RoleDto getRoleByName(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }
        return mapToDto(role);
    }

    // dùng để gán role cho user
    @Override
    public Role getRoleEntityById(int roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }
        return role;
    }

    @Override
    public List<RoleDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(this::mapToDto).toList();
    }

    @Override
    public RoleDto updateRole(int roleId, RoleDto roleDto) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }

        // ❌ Bug tương tự các entities khác - luôn check duplicate
//        if (roleDto.getRoleName() != null && !roleDto.getRoleName().isEmpty()) {
//            if (roleRepository.existsByRoleName(roleDto.getRoleName())) {
//                throw new AppException(ErrorCode.ROLE_EXISTED); // ← Sẽ fail khi update cùng tên
//            }
//            role.setRoleName(roleDto.getRoleName());
//        }

        // ✅ Phải check nếu tên khác với tên hiện tại
        if (roleDto.getRoleName() != null && !roleDto.getRoleName().trim().isEmpty()) {
            if (!role.getRoleName().equals(roleDto.getRoleName()) &&
                    roleRepository.existsByRoleName(roleDto.getRoleName())) {
                throw new AppException(ErrorCode.ROLE_EXISTED);
            }
            role.setRoleName(roleDto.getRoleName());
        }

        roleRepository.save(role);
        return mapToDto(role);
    }

    @Override
    public void deleteRole(int roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }
        roleRepository.delete(role);
    }

    private RoleDto mapToDto(Role role) {
        return RoleDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build();
    }
}
