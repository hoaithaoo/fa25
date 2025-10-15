package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionService {

    InventoryTransactionDto createInventoryTransaction(InventoryTransactionDto dto); // ✅ Đổi tham số

    InventoryTransactionDto getInventoryTransactionById(int inventoryId);

    InventoryTransaction getInventoryTransactionEntityById(int inventoryId);

    List<InventoryTransactionDto> getAllInventoryTransactions();

    List<InventoryTransactionDto> getInventoryTransactionsByStoreStock(int storeStockId);

    List<InventoryTransactionDto> getInventoryTransactionsByDateRange(
            LocalDateTime start, LocalDateTime end);

    InventoryTransactionDto updateInventoryTransaction(
            int inventoryId, InventoryTransactionDto dto); // ✅ Đổi tham số

    void deleteInventoryTransaction(int inventoryId);
}
