package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransactionContract;

import java.util.Optional;

@Repository
public interface InventoryTransactionContractRepository extends JpaRepository<InventoryTransactionContract, Integer> {
    Optional<InventoryTransactionContract> findByInventoryTransaction_InventoryId(int inventoryId);
    boolean existsByContractFileUrl(String contractFileUrl);
}

