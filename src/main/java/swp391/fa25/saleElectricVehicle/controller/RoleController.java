package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.RoleDto;
import swp391.fa25.saleElectricVehicle.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    RoleService roleService;

    @PostMapping("/create")
    public RoleDto createRole(@RequestBody RoleDto roleDto) {
        return roleService.createRole(roleDto);
    }

    @GetMapping("/{roleId}")
    public RoleDto getRoleById(@PathVariable int roleId) {
        return roleService.findRoleById(roleId);
    }

    @GetMapping("/all")
    public List<RoleDto> getAllRoles() {
        return roleService.findAllRoles();
    }

    @PutMapping("/update/{roleId}")
    public RoleDto updateRole(@PathVariable int roleId, @RequestBody RoleDto roleDto) {
        return roleService.updateRole(roleId, roleDto);
    }

    @DeleteMapping("/delete/{roleId}")
    public  void deleteRole(@PathVariable int roleId) {
        roleService.deleteRole(roleId);
    }
}
