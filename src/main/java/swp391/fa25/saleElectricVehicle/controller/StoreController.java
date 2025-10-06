package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    StoreService storeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StoreDto>> createStore(@RequestBody StoreDto storeDto) {
        StoreDto createdStore = storeService.createStore(storeDto);
        ApiResponse<StoreDto> response = ApiResponse.<StoreDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Store created successfully")
                .data(createdStore)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<List<StoreDto>>> getStoreByName(String storeName) {
        List<StoreDto> stores = storeService.findStoreByName(storeName);
        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Stores retrieved successfully")
                .data(stores)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StoreDto>>> getAllStores() {
        List<StoreDto> stores = storeService.findAllStores();
        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
                .code(HttpStatus.OK.value())
                .message("All stores retrieved successfully")
                .data(stores)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{storeId}")
    public ResponseEntity<ApiResponse<StoreDto>> updateStore(@PathVariable int storeId, @RequestBody StoreDto storeDto) {
        StoreDto updatedStore = storeService.updateStore(storeId, storeDto);
        ApiResponse<StoreDto> response = ApiResponse.<StoreDto>builder()
                .code(HttpStatus.OK.value())
                .message("Store updated successfully")
                .data(updatedStore)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable int storeId) {
        storeService.deleteStore(storeId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Store deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}