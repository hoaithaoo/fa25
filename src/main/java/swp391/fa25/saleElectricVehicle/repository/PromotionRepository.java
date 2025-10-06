package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Promotion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    // Find active promotions
    List<Promotion> findByIsActiveTrue();

    // Find promotions by date range
    List<Promotion> findByStartDateBeforeAndEndDateAfter(LocalDateTime currentDate1, LocalDateTime currentDate2);

    // Find by store
    List<Promotion> findByStore_StoreId(int storeId);
    List<Promotion> findByPromotionNameContainingIgnoreCase(String promotionName);
    boolean existsByPromotionNameIgnoreCase(String promotionName);
    List<Promotion> findByModel_ModelId(int modelId);
}