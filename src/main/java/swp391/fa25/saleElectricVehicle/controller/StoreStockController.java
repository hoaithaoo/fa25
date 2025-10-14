package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.StoreStockService;

import java.math.BigDecimal;
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

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<StoreStockDto>>> getAllStoreStock() {
        List<StoreStockDto> storeStockDto = storeStockService.getAllStoreStock();
        ApiResponse<List<StoreStockDto>> response = ApiResponse.<List<StoreStockDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all store stock successfully")
                .data(storeStockDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{stockId}/update-price")
    public ResponseEntity<ApiResponse<StoreStockDto>> updatePriceOfStore(@PathVariable int stockId, @RequestParam BigDecimal price) {
        StoreStockDto updatedStoreStock = storeStockService.updatePriceOfStore(stockId, price);
        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
                .code(HttpStatus.OK.value())
                .message("Store stock price updated successfully")
                .data(updatedStoreStock)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{stockId}/update-quantity")
    public ResponseEntity<ApiResponse<StoreStockDto>> updateQuantity(@PathVariable int stockId, @RequestParam int quantity) {
        StoreStockDto updatedStoreStock = storeStockService.updateQuantity(stockId, quantity);
        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
                .code(HttpStatus.OK.value())
                .message("Store stock quantity updated successfully")
                .data(updatedStoreStock)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{stockId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteStoreStock(@PathVariable int stockId) {
        storeStockService.deleteStoreStock(stockId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Store stock deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
