package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp391.fa25.saleElectricVehicle.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
