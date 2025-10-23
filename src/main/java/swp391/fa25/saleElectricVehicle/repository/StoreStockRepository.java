package swp391.fa25.saleElectricVehicle.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.StoreStock;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StoreStockRepository extends JpaRepository<StoreStock, Integer> {
    StoreStock findByStore_StoreIdAndModelColor_ModelColorId(int storeId, int modelColorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StoreStock s WHERE s.store.storeId = :storeId AND s.modelColor.modelColorId = :modelColorId")
    StoreStock findByStore_StoreIdAndModelColor_ModelColorIdWithLock(int storeId, int modelColorId);

    // Find by Store
    List<StoreStock> findByStore_StoreId(int storeId);

    // Find by ModelColor
    List<StoreStock> findByModelColor_ModelColorId(int modelColorId);

    // Find available stock (quantity > 0)
    List<StoreStock> findByQuantityGreaterThan(int quantity);

    // Find by price range
    List<StoreStock> findByPriceOfStoreBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find by quantity range
    List<StoreStock> findByQuantityBetween(int minQuantity, int maxQuantity);

    // Check if exists by store and modelColor
    boolean existsByStore_StoreIdAndModelColor_ModelColorId(int storeId, int modelColorId);

    // Count by store
    long countByStore_StoreId(int storeId);
}