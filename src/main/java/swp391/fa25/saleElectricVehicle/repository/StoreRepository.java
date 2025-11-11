package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.StoreStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    boolean existsByStoreName(String storeName);
    List<Store> findStoresByStoreNameContaining(String name);
    Store findStoreByUser_UserId(int userId);
    List<Store> findStoresByStatus(StoreStatus status);
    
    // Deactivate stores with expired contracts
    @Modifying
    @Query("UPDATE Store s " +
            "SET s.status = 'INACTIVE', s.updatedAt = :currentDate " +
            "WHERE s.contractEndDate < :currentDate AND s.status = 'ACTIVE'")
    int deactivateStoresWithExpiredContracts(@Param("currentDate") LocalDateTime currentDate);
    
    // Activate stores with valid contracts
    @Modifying
    @Query("UPDATE Store s " +
            "SET s.status = 'ACTIVE', s.updatedAt = :currentDate " +
            "WHERE s.contractStartDate <= :currentDate AND s.contractEndDate >= :currentDate AND s.status = 'INACTIVE'")
    int activateStoresWithValidContracts(@Param("currentDate") LocalDateTime currentDate);
}
