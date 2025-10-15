package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    // Truy vấn mở rộng ví dụ: lấy các transaction trong một khoảng ngày
    List<InventoryTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

    // Truy vấn mở rộng ví dụ: lấy transaction theo storeStockId
    List<InventoryTransaction> findByStoreStock_StoreStockId(int storeStockId);

    // Có thể bổ sung các hàm query dạng Spring Data khi cần!
}
