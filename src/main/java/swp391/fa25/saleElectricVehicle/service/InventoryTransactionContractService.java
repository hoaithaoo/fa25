package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.InventoryTransactionContract;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionContractDto;
import swp391.fa25.saleElectricVehicle.payload.request.inventorytransactioncontract.SignInventoryTransactionContractRequest;

public interface InventoryTransactionContractService {
    InventoryTransactionContractDto createDraftContract(int inventoryId);
    InventoryTransactionContractDto signContract(int inventoryId, SignInventoryTransactionContractRequest request);
    String getContractHtml(int inventoryId);
    InventoryTransactionContractDto uploadSignedContract(int inventoryId, String fileUrl);
    InventoryTransactionContractDto getContractByInventoryId(int inventoryId);
    InventoryTransactionContract getContractEntityByInventoryId(int inventoryId);
}

