package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionService {

    InventoryTransactionDto createInventoryTransaction(InventoryTransactionDto dto);

    InventoryTransactionDto getInventoryTransactionById(int inventoryId);

    InventoryTransaction getInventoryTransactionEntityById(int inventoryId);

    List<InventoryTransactionDto> getAllInventoryTransactions();

    List<InventoryTransactionDto> getInventoryTransactionsByStoreStock(int storeStockId);

    List<InventoryTransactionDto> getInventoryTransactionsByDateRange(
            LocalDateTime start, LocalDateTime end);

    InventoryTransactionDto updateInventoryTransaction(
            int inventoryId, InventoryTransactionDto dto);

    void deleteInventoryTransaction(int inventoryId);

    InventoryTransactionDto acceptRequest(int inventoryId);

    InventoryTransactionDto rejectRequest(int inventoryId);

    InventoryTransactionDto startShipping(int inventoryId);

    InventoryTransactionDto confirmDelivery(int inventoryId);
}
