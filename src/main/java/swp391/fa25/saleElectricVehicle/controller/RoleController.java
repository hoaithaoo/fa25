package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.RoleDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoleDto>> createRole(@RequestBody RoleDto roleDto) {
        RoleDto createdRole = roleService.createRole(roleDto);
        ApiResponse<RoleDto> response = ApiResponse.<RoleDto>builder()
                .code(HttpStatus.OK.value())
                .message("Role created successfully")
                .data(createdRole)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleByName(@PathVariable int roleId) {
        RoleDto role = roleService.findRoleByName(String.valueOf(roleId));
        ApiResponse<RoleDto> response = ApiResponse.<RoleDto>builder()
                .code(HttpStatus.OK.value())
                .message("Role fetched successfully")
                .data(role)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
        List<RoleDto> roles = roleService.findAllRoles();
        ApiResponse<List<RoleDto>> response = ApiResponse.<List<RoleDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Roles fetched successfully")
                .data(roles)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{roleId}")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(@PathVariable int roleId, @RequestBody RoleDto roleDto) {
        RoleDto updatedRole = roleService.updateRole(roleId, roleDto);
        ApiResponse<RoleDto> response = ApiResponse.<RoleDto>builder()
                .code(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable int roleId) {
        roleService.deleteRole(roleId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
