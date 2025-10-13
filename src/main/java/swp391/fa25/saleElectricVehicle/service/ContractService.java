package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;

import java.util.List;

public interface ContractService {
    ContractDto createContract(ContractDto contractDto);
    ContractDto updateContract(int id, ContractDto contractDto);
    ContractDto getContractById(int id);
    ContractDto getContractByFileUrl(String fileUrl);
    List<ContractDto> getAllContracts();
    List<ContractDto> getContractsByStatus(Contract.ContractStatus status);
    void deleteContractById(int id);
    ContractDto updateContractStatus(int id, Contract.ContractStatus status);
}
