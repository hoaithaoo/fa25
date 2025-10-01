package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByRoleName(String roleName);
}
