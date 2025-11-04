package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.request.contract.CreateContractRequest;
import swp391.fa25.saleElectricVehicle.payload.response.contract.GetContractResponse;

import java.util.List;

public interface ContractService {
    ContractDto createDraftContract(CreateContractRequest request);
//    ContractDto updateContract(int id, ContractDto contractDto);
    ContractDto getContractById(int id);
    ContractDto addFileUrlContract(int id, String fileUrl);
//    ContractDto getContractByFileUrl(String fileUrl);
    List<GetContractResponse> getAllContracts();
//    List<ContractDto> getContractsByStatus(Contract.ContractStatus status);
    void deleteContractById(int id);
//    ContractDto updateContractStatus(int id, Contract.ContractStatus status);
}
