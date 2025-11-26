package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.request.contract.CreateContractRequest;

import java.util.List;

public interface ContractService {
    ContractDto createContract(CreateContractRequest request);
    Contract getContractEntityById(int id);
    ContractDto getContractById(int id);
    ContractDto getContractDetailById(int id);
    ContractDto addFileUrlContract(int id, String fileUrl);
    List<ContractDto> getAllContracts();
    void updateContractStatus(Contract contract, ContractStatus status);
    // Check if deposit contract exists for order
    boolean hasDepositContract(int orderId);
    // Get deposit contract by order ID
    Contract getDepositContractByOrderId(int orderId);
    // Check if sale contract exists for order
    boolean hasSaleContract(int orderId);
    // Get sale contract by order ID
    Contract getSaleContractByOrderId(int orderId);
}
