package swp391.fa25.saleElectricVehicle.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionContractDto;
import swp391.fa25.saleElectricVehicle.payload.request.inventorytransactioncontract.SignInventoryTransactionContractRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.CloudinaryService;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionContractService;

@RestController
@RequestMapping("/inventory-transactions")
public class InventoryTransactionContractController {

    @Autowired
    private InventoryTransactionContractService contractService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // EVM tạo draft contract
    @PostMapping("/{inventoryId}/create-contract")
    public ResponseEntity<ApiResponse<InventoryTransactionContractDto>> createContract(
            @PathVariable int inventoryId) {
        InventoryTransactionContractDto contract = contractService.createDraftContract(inventoryId);
        ApiResponse<InventoryTransactionContractDto> response = ApiResponse.<InventoryTransactionContractDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Contract created successfully")
                .data(contract)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // EVM ký hợp đồng
    @PostMapping("/{inventoryId}/sign-contract")
    public ResponseEntity<ApiResponse<InventoryTransactionContractDto>> signContract(
            @PathVariable int inventoryId,
            @RequestBody SignInventoryTransactionContractRequest request) {
        InventoryTransactionContractDto contract = contractService.signContract(inventoryId, request);
        ApiResponse<InventoryTransactionContractDto> response = ApiResponse.<InventoryTransactionContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Contract signed successfully")
                .data(contract)
                .build();
        return ResponseEntity.ok(response);
    }

    // Manager download HTML contract
    @GetMapping("/{inventoryId}/contract/html")
    public ResponseEntity<String> getContractHtml(@PathVariable int inventoryId) {
        String htmlContent = contractService.getContractHtml(inventoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }

    // Manager upload file đã ký
    @PostMapping(
            value = "/{inventoryId}/upload-contract",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<InventoryTransactionContractDto>> uploadSignedContract(
            @PathVariable int inventoryId,
            @Parameter(description = "File hợp đồng đã ký (HTML hoặc PDF)", required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file) {
        String fileUrl = cloudinaryService.uploadFile(file, "inventory-contracts");
        InventoryTransactionContractDto contract = contractService.uploadSignedContract(inventoryId, fileUrl);
        ApiResponse<InventoryTransactionContractDto> response = ApiResponse.<InventoryTransactionContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Contract uploaded successfully")
                .data(contract)
                .build();
        return ResponseEntity.ok(response);
    }

    // Lấy contract info
    @GetMapping("/{inventoryId}/contract")
    public ResponseEntity<ApiResponse<InventoryTransactionContractDto>> getContract(
            @PathVariable int inventoryId) {
        InventoryTransactionContractDto contract = contractService.getContractByInventoryId(inventoryId);
        ApiResponse<InventoryTransactionContractDto> response = ApiResponse.<InventoryTransactionContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get contract successfully")
                .data(contract)
                .build();
        return ResponseEntity.ok(response);
    }
}

