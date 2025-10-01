package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto createRole(RoleDto roleDto);
    RoleDto findRoleById(int roleId);
//    RoleDto findRoleByName(String roleName);
    List<RoleDto> findAllRoles();
    RoleDto updateRole(int roleId, RoleDto roleDto);
    void deleteRole(int roleId);
}
