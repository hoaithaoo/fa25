package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Model;
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
    List<Promotion> findByStore_StoreIdAndPromotionNameContainingIgnoreCase(int storeId, String promotionName);
    boolean existsByPromotionNameIgnoreCase(String promotionName);
//    List<Promotion> findByModel_ModelId(int modelId);
    List<Promotion> findByModel(Model model);
    List<Promotion> findByStore_StoreIdAndModel(int storeId, Model model);
    
    // Deactivate expired promotions (chỉ các promotion chưa bị tắt thủ công)
    @Modifying
    @Query("UPDATE Promotion p " +
            "SET p.isActive = false, p.updatedAt = :currentDate " +
            "WHERE p.endDate < :currentDate AND p.isActive = true AND p.isManuallyDisabled = false")
    int deactivateExpiredPromotions(@Param("currentDate") LocalDateTime currentDate);

    // Activate current promotions (chỉ các promotion chưa bị tắt thủ công)
    @Modifying
    @Query("UPDATE Promotion p " +
            "SET p.isActive = true, p.updatedAt = :currentDate " +
            "WHERE p.startDate <= :currentDate AND p.endDate >= :currentDate " +
            "AND p.isActive = false AND p.isManuallyDisabled = false")
    int activateCurrentPromotions(@Param("currentDate") LocalDateTime currentDate);
}