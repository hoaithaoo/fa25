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
        roleRepository.save(newRole);
        return roleDto;
    }

    @Override
    public RoleDto findRoleByName(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }
        return RoleDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build();
    }

    @Override
    public List<RoleDto> findAllRoles() {
        return roleRepository.findAll().stream().map(role -> {
            RoleDto roleDto = RoleDto.builder()
                    .roleId(role.getRoleId())
                    .roleName(role.getRoleName())
                    .build();
            return roleDto;
        }).toList();
    }

    @Override
    public RoleDto updateRole(int roleId, RoleDto roleDto) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }

        if (roleDto.getRoleName() != null && !roleDto.getRoleName().isEmpty()) {
            if (roleRepository.existsByRoleName(roleDto.getRoleName())) {
                throw new AppException(ErrorCode.ROLE_EXISTED);
            }
            role.setRoleName(roleDto.getRoleName());
        }

        roleRepository.save(role);
        return RoleDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build();
    }

    @Override
    public void deleteRole(int roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_EXIST);
        }
        roleRepository.delete(role);
    }
}
