package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Role;
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
            throw new RuntimeException("Role name: " + roleDto.getRoleName() + " already exists");
        }
        Role newRole = Role.builder()
                .roleName(roleDto.getRoleName())
                .build();
        Role savedRole = roleRepository.save(newRole);
//        return roleDto;
        return RoleDto.builder()
                .roleId(savedRole.getRoleId()) // ← Có roleId từ DB
                .roleName(savedRole.getRoleName())
                .build();
    }


    @Override
    public RoleDto findRoleById(int roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new RuntimeException("Role with id: " + roleId + " not found");
        } else {
            RoleDto roleDto = RoleDto.builder()
                    .roleId(role.getRoleId())
                    .roleName(role.getRoleName())
                    .build();
            return roleDto;
        }
    }

//    @Override
//    public RoleDto findRoleByName(String roleName) {
//        if (roleRepository.existsByRoleName(roleName)) {
//            Role role = roleRepository.findAll().stream()
//                    .filter(r -> r.getRoleName().equals(roleName))
//                    .findFirst()
//                    .orElse(null);
//            RoleDto roleDto = RoleDto.builder()
//                    .roleId(role.getRoleId())
//                    .roleName(role.getRoleName())
//                    .build();
//            return roleDto;
//        } else {
//            throw new RuntimeException("Role name: " + roleName + " not found");
//        }
//    }

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
            throw new RuntimeException("Role with id: " + roleId + " not found");
        } else {
            if (!role.getRoleName().equals(roleDto.getRoleName())
                    && roleRepository.existsByRoleName(roleDto.getRoleName())) {
                throw new RuntimeException("Role name: " + roleDto.getRoleName() + " already exists");
            }
            role.setRoleName(roleDto.getRoleName());
            roleRepository.save(role);
        }
        return RoleDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build();
    }

    @Override
    public void deleteRole(int roleId) {
        if (roleRepository.existsById(roleId)) {
            roleRepository.deleteById(roleId);
        } else {
            throw new RuntimeException("Role with id: " + roleId + " not found");
        }
    }
}
