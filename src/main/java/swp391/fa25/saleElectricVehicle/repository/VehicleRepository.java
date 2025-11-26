package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swp391.fa25.saleElectricVehicle.entity.Vehicle;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("SELECT v FROM Vehicle v " +
           "WHERE v.storeStock.store.storeId = :storeId " +
           "AND v.storeStock.modelColor.model.modelId = :modelId " +
           "AND v.storeStock.modelColor.color.colorId = :colorId " +
           "AND v.status = :status " +
           "ORDER BY v.importDate ASC")
    List<Vehicle> findByStoreStock_Store_StoreIdAndStoreStock_ModelColor_Model_ModelIdAndStoreStock_ModelColor_Color_ColorIdAndStatusOrderByImportDateAsc(
            @Param("storeId") int storeId, 
            @Param("modelId") int modelId, 
            @Param("colorId") int colorId, 
            @Param("status") VehicleStatus status);
}
