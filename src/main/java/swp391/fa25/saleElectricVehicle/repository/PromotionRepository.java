package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    // Deactivate expired promotions
    @Modifying
    @Query("UPDATE Promotion p " +
            "SET p.isActive = false, p.updatedAt = :now " +
            "WHERE p.endDate <= :currentDate AND p.isActive = true")
    int deactivateExpiredPromotions(LocalDateTime currentDate);

    // Activate current promotions
    @Modifying
    @Query("UPDATE Promotion p " +
            "SET p.isActive = true, p.updatedAt = :now " +
            "WHERE p.startDate <= :currentDate AND p.endDate >= :currentDate AND p.isActive = false")
    int activateCurrentPromotions(LocalDateTime currentDate);
}