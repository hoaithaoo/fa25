package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;
import swp391.fa25.saleElectricVehicle.payload.dto.PaymentInfoDto;
import swp391.fa25.saleElectricVehicle.payload.request.inventory.CreateInventoryTransactionRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionService {

    InventoryTransactionDto createInventoryTransaction(CreateInventoryTransactionRequest request);

    InventoryTransactionDto getInventoryTransactionById(int inventoryId);

    InventoryTransaction getInventoryTransactionEntityById(int inventoryId);

    List<InventoryTransactionDto> getAllInventoryTransactions();

    List<InventoryTransactionDto> getInventoryTransactionsByStoreStock(int storeStockId);

    List<InventoryTransactionDto> getInventoryTransactionsByDateRange(
            LocalDateTime start, LocalDateTime end);

//    InventoryTransactionDto updateInventoryTransaction(
//            int inventoryId, InventoryTransactionDto dto);

    void deleteInventoryTransaction(int inventoryId);

    InventoryTransactionDto acceptRequest(int inventoryId);

    InventoryTransactionDto rejectRequest(int inventoryId);

    InventoryTransactionDto startShipping(int inventoryId);

    InventoryTransactionDto confirmDelivery(int inventoryId);

    InventoryTransactionDto uploadReceipt(int inventoryId, String imageUrl);

    InventoryTransactionDto confirmPayment(int inventoryId);

    InventoryTransactionDto cancelRequest(int inventoryId);

    PaymentInfoDto getPaymentInfo(int inventoryId);

    // Update status to CONTRACT_SIGNED when contract is signed
    InventoryTransactionDto updateStatusToContractSigned(int inventoryId);

    // Update status to EVM_SIGNED when EVM creates contract
    InventoryTransactionDto updateStatusToEvmSigned(int inventoryId);
}
