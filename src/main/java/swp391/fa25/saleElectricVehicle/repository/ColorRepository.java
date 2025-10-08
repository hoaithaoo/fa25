package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp391.fa25.saleElectricVehicle.entity.Color;
import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;

import java.util.List;

public interface ColorRepository extends JpaRepository<Color, Integer> {
    boolean existsColorByColorName(String colorName);
    List<Color> findColorsByColorNameContaining(String colorName);
    Color findColorByColorName(String colorName);
}
