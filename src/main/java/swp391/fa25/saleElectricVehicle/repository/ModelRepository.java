package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {
    boolean existsModelByModelName(String modelName);
    Model findByModelName(String modelName);
}
