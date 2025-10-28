package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.ContractService;

import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ContractDto>> createContract(@RequestBody ContractDto contractDto) {
        ContractDto createdContract = contractService.createContract(contractDto);
        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create contract successfully")
                .data(createdContract)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractDto>> getContractById(@PathVariable int id) {
        ContractDto contractDto = contractService.getContractById(id);
        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get contract successfully")
                .data(contractDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/file")
    public ResponseEntity<ApiResponse<ContractDto>> getContractByFileUrl(@RequestParam String fileUrl) {
        ContractDto contractDto = contractService.getContractByFileUrl(fileUrl);
        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get contract successfully")
                .data(contractDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ContractDto>>> getAllContracts() {
        List<ContractDto> contracts = contractService.getAllContracts();
        ApiResponse<List<ContractDto>> response = ApiResponse.<List<ContractDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Contracts fetched successfully")
                .data(contracts)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ContractDto>>> getContractsByStatus(@PathVariable Contract.ContractStatus status) {
        List<ContractDto> contracts = contractService.getContractsByStatus(status);
        ApiResponse<List<ContractDto>> response = ApiResponse.<List<ContractDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Contracts fetched successfully by status")
                .data(contracts)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContract(@PathVariable int id) {
        contractService.deleteContractById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete contract successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ContractDto>> updateContract(@PathVariable int id, @RequestBody ContractDto contractDto) {
        ContractDto updatedContract = contractService.updateContract(id, contractDto);
        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Update contract successfully")
                .data(updatedContract)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ContractDto>> updateContractStatus(
            @PathVariable int id,
            @RequestParam Contract.ContractStatus status) {
        ContractDto updatedContract = contractService.updateContractStatus(id, status);
        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Update contract status successfully")
                .data(updatedContract)
                .build();
        return ResponseEntity.ok(response);
    }
}
