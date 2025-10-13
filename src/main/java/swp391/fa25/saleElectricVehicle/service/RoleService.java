package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Role;
import swp391.fa25.saleElectricVehicle.payload.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto createRole(RoleDto roleDto);
//    RoleDto findRoleById(int roleId);
    RoleDto getRoleByName(String roleName);
    Role getRoleEntityById(int roleId);
    List<RoleDto> getAllRoles();
    RoleDto updateRole(int roleId, RoleDto roleDto);
    void deleteRole(int roleId);
}
