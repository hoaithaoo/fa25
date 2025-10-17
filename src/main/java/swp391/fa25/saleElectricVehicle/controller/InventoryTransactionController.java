package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryTransactionController {

    @Autowired
    private InventoryTransactionService inventoryTransactionService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> createInventoryTransaction(
            @Valid @RequestBody InventoryTransactionDto dto) {

        InventoryTransactionDto created = inventoryTransactionService.createInventoryTransaction(dto);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Inventory transaction created successfully")
                .data(created)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> getInventoryTransactionById(
            @PathVariable int inventoryId) {

        InventoryTransactionDto dto = inventoryTransactionService.getInventoryTransactionById(inventoryId);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Inventory transaction fetched successfully")
                .data(dto)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<InventoryTransactionDto>>> getAllInventoryTransactions() {

        List<InventoryTransactionDto> dtos = inventoryTransactionService.getAllInventoryTransactions();

        ApiResponse<List<InventoryTransactionDto>> response =
                ApiResponse.<List<InventoryTransactionDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("All inventory transactions fetched successfully")
                        .data(dtos)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/store-stock/{storeStockId}")
    public ResponseEntity<ApiResponse<List<InventoryTransactionDto>>> getByStoreStock(
            @PathVariable int storeStockId) {

        List<InventoryTransactionDto> dtos =
                inventoryTransactionService.getInventoryTransactionsByStoreStock(storeStockId);

        ApiResponse<List<InventoryTransactionDto>> response =
                ApiResponse.<List<InventoryTransactionDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Inventory transactions by store stock fetched successfully")
                        .data(dtos)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<InventoryTransactionDto>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<InventoryTransactionDto> dtos =
                inventoryTransactionService.getInventoryTransactionsByDateRange(start, end);

        ApiResponse<List<InventoryTransactionDto>> response =
                ApiResponse.<List<InventoryTransactionDto>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Inventory transactions by date range fetched successfully")
                        .data(dtos)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> updateInventoryTransaction(
            @PathVariable int inventoryId,
            @Valid @RequestBody InventoryTransactionDto dto) {

        InventoryTransactionDto updated =
                inventoryTransactionService.updateInventoryTransaction(inventoryId, dto);

        ApiResponse<InventoryTransactionDto> response =
                ApiResponse.<InventoryTransactionDto>builder()
                        .code(HttpStatus.OK.value())
                        .message("Inventory transaction updated successfully")
                        .data(updated)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{inventoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteInventoryTransaction(@PathVariable int inventoryId) {

        inventoryTransactionService.deleteInventoryTransaction(inventoryId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Inventory transaction deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
