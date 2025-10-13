package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.StoreStockService;

import java.util.List;

@RestController
@RequestMapping("/api/store-stocks")
public class StoreStockController {

    @Autowired
    private StoreStockService storeStockService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StoreStockDto>> createStoreStock(@RequestBody StoreStockDto createStoreStock) {
        StoreStockDto storeStockDto = storeStockService.createStoreStock(createStoreStock);
        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Store stock created successfully")
                .data(storeStockDto)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ - Get by ID
    @GetMapping("/{stockId}")
    public ResponseEntity<ApiResponse<StoreStockDto>> getStoreStockById(@PathVariable int stockId) {
        StoreStockDto storeStock = storeStockService.getStoreStockById(stockId);
        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
                .code(HttpStatus.OK.value())
                .message("Store stock retrieved successfully")
                .data(storeStock)
                .build();
        return ResponseEntity.ok(response);
    }

    // READ - Get all
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StoreStockDto>>> getAllStoreStocks() {
        List<StoreStockDto> storeStocks = storeStockService.getAllStoreStocks();
        ApiResponse<List<StoreStockDto>> response = ApiResponse.<List<StoreStockDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Store stocks retrieved successfully")
                .data(storeStocks)
                .build();
        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/{stockId}")
    public ResponseEntity<ApiResponse<StoreStockDto>> updateStoreStock(
            @PathVariable int stockId,
            @RequestBody StoreStockDto storeStockDto) {

        StoreStockDto updated = storeStockService.updateStoreStock(stockId, storeStockDto);
        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
                .code(HttpStatus.OK.value())
                .message("Store stock updated successfully")
                .data(updated)
                .build();
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{stockId}")
    public ResponseEntity<ApiResponse<Void>> deleteStoreStock(@PathVariable int stockId) {
        storeStockService.deleteStoreStock(stockId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Store stock deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

}
