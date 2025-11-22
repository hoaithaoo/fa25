package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.request.contract.CreateContractRequest;
import swp391.fa25.saleElectricVehicle.payload.response.contract.GetContractDetailResponse;
import swp391.fa25.saleElectricVehicle.payload.response.contract.GetContractResponse;

import java.util.List;

public interface ContractService {
    ContractDto createDraftContract(CreateContractRequest request);
    Contract getContractEntityById(int id);
    ContractDto getContractById(int id);
    GetContractDetailResponse getContractDetailById(int id);
    ContractDto addFileUrlContract(int id, String fileUrl);
    List<GetContractResponse> getAllContracts();
    void updateContractStatus(Contract contract, ContractStatus status);
}
