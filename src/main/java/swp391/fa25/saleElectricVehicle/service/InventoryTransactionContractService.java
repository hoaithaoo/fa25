package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.InventoryTransactionContract;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionContractDto;

public interface InventoryTransactionContractService {
    InventoryTransactionContractDto createDraftContract(int inventoryId);
    String getContractHtml(int inventoryId);
    InventoryTransactionContractDto uploadSignedContract(int inventoryId, String fileUrl);
    InventoryTransactionContractDto getContractByInventoryId(int inventoryId);
    InventoryTransactionContract getContractEntityByInventoryId(int inventoryId);
}

