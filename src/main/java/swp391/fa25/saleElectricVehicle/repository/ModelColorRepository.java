package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.ModelColor;

import java.util.List;

@Repository
public interface ModelColorRepository extends JpaRepository<ModelColor, Integer> {

    ModelColor findByModel_ModelIdAndColor_ColorId(int modelId, int colorId);
    List<ModelColor> findByColor_ColorId(int colorId);
    List<ModelColor> findByModel_ModelId(int modelId);
}
