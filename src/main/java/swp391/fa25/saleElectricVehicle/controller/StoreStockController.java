package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;
import swp391.fa25.saleElectricVehicle.payload.request.stock.CreateStoreStockRequest;
import swp391.fa25.saleElectricVehicle.payload.request.stock.UpdatePriceOfStoreRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.StoreStockService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/store-stocks")
public class StoreStockController {

    @Autowired
    private StoreStockService storeStockService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StoreStockDto>> createStoreStock(@RequestBody CreateStoreStockRequest createStoreStock) {
        StoreStockDto storeStockDto = storeStockService.createStoreStock(createStoreStock);
        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Store stock created successfully")
                .data(storeStockDto)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("quantity")
    public ResponseEntity<ApiResponse<Integer>> getQuantityByStoreId(@RequestBody int modelId, @RequestBody int colorId) {
        int quantity = storeStockService.getQuantityByModelIdAndColorId(modelId, colorId);
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .code(HttpStatus.OK.value())
                .message("Get quantity successfully")
                .data(quantity)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<StoreStockDto>>> getAllStoreStockByStoreId() {
        List<StoreStockDto> storeStockDto = storeStockService.getAllStoreStockByStoreId();
        ApiResponse<List<StoreStockDto>> response = ApiResponse.<List<StoreStockDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all store stock successfully")
                .data(storeStockDto)
                .build();
        return ResponseEntity.ok(response);
    }

    // chỉ được update price theo store hiện tại
    @PutMapping("/update-price")
    public ResponseEntity<ApiResponse<StoreStockDto>> updatePriceOfStore(@RequestBody UpdatePriceOfStoreRequest request) {
        StoreStockDto updatedStoreStock = storeStockService.updatePriceOfStore(request);
        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
                .code(HttpStatus.OK.value())
                .message("Store stock price updated successfully")
                .data(updatedStoreStock)
                .build();
        return ResponseEntity.ok(response);
    }

    // không cho update thẳng quantity, chỉ gọi nội bộ khi có đơn hàng hoặc nhập kho
//    @PutMapping("/{stockId}/update-quantity")
//    public ResponseEntity<ApiResponse<StoreStockDto>> updateQuantity(@PathVariable int stockId, @RequestParam int quantity) {
//        StoreStockDto updatedStoreStock = storeStockService.updateQuantity(stockId, quantity);
//        ApiResponse<StoreStockDto> response = ApiResponse.<StoreStockDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Store stock quantity updated successfully")
//                .data(updatedStoreStock)
//                .build();
//        return ResponseEntity.ok(response);
//    }

//    @DeleteMapping("/{stockId}/delete")
//    public ResponseEntity<ApiResponse<Void>> deleteStoreStock(@PathVariable int stockId) {
//        storeStockService.deleteStoreStock(stockId);
//        ApiResponse<Void> response = ApiResponse.<Void>builder()
//                .code(HttpStatus.NO_CONTENT.value())
//                .message("Store stock deleted successfully")
//                .data(null)
//                .build();
//        return ResponseEntity.ok(response);
//    }
}