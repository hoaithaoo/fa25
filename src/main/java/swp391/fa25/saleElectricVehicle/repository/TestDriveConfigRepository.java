package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.TestDriveConfig;

@Repository
public interface TestDriveConfigRepository extends JpaRepository<TestDriveConfig, Integer> {
    TestDriveConfig findByStore_StoreId(int storeId);
}
