package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.StoreStatus;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    boolean existsByStoreName(String storeName);
    List<Store> findStoresByStoreNameContaining(String name);
    Store findStoreByUser_UserId(int userId);
    List<Store> findStoresByStatus(StoreStatus status);
}
