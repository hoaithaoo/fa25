package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.RoleDto;
import swp391.fa25.saleElectricVehicle.service.RoleService;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class RoleController {

    @Autowired
    RoleService roleService;

//    @PostMapping("/create")
//    public RoleDto createRole(@RequestBody RoleDto roleDto) {
//        return roleService.createRole(roleDto);
//    }
//
//    @GetMapping("/{roleId}")
//    public RoleDto getRoleById(@PathVariable int roleId) {
//        return roleService.findRoleById(roleId);
//    }
//
//    @GetMapping("/all")
//    public List<RoleDto> getAllRoles() {
//        return roleService.findAllRoles();
//    }
//
//    @PutMapping("/update/{roleId}")
//    public RoleDto updateRole(@PathVariable int roleId, @RequestBody RoleDto roleDto) {
//        return roleService.updateRole(roleId, roleDto);
//    }
//
//    @DeleteMapping("/delete/{roleId}")
//    public  void deleteRole(@PathVariable int roleId) {
//        roleService.deleteRole(roleId);
//    }
// CREATE
@PostMapping("/create")
public ResponseEntity<ApiResponse<RoleDto>> createRole(@RequestBody RoleDto roleDto) {
    RoleDto createdRole = roleService.createRole(roleDto);
    ApiResponse<RoleDto> response = ApiResponse.<RoleDto>builder()
            .code(HttpStatus.CREATED.value())
            .message("Role created successfully")
            .data(createdRole)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

    // READ - Get by ID
    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleById(@PathVariable int roleId) {
        RoleDto roleDto = roleService.findRoleById(roleId);
        ApiResponse<RoleDto> response = ApiResponse.<RoleDto>builder()
                .code(HttpStatus.OK.value())
                .message("Role retrieved successfully")
                .data(roleDto)
                .build();
        return ResponseEntity.ok(response);
    }

    // READ - Get all
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
        List<RoleDto> roles = roleService.findAllRoles();
        ApiResponse<List<RoleDto>> response = ApiResponse.<List<RoleDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Roles retrieved successfully")
                .data(roles)
                .build();
        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/update/{roleId}")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(
            @PathVariable int roleId,
            @RequestBody RoleDto roleDto) {

        RoleDto updatedRole = roleService.updateRole(roleId, roleDto);
        ApiResponse<RoleDto> response = ApiResponse.<RoleDto>builder()
                .code(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
        return ResponseEntity.ok(response);
    }

    // DELETE
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

    // BUSINESS - Get users by role (nếu cần)
    @GetMapping("/{roleId}/users")
    public ResponseEntity<ApiResponse<List<Object>>> getUsersByRole(@PathVariable int roleId) {
        // Có thể implement sau khi có UserService method
        ApiResponse<List<Object>> response = ApiResponse.<List<Object>>builder()
                .code(HttpStatus.OK.value())
                .message("Users by role retrieved successfully")
                .data(List.of()) // Placeholder
                .build();
        return ResponseEntity.ok(response);
    }

}

