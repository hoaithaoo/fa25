package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Store;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    boolean existsByStoreName(String storeName);
    boolean existsByPhone(String phone);
    List<Store> findStoresByStoreNameContaining(String name);
    Store findStoreByStoreName(String storeName);
}
