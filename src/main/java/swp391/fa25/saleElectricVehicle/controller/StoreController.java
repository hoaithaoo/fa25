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
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.store.StoreMonthlyRevenueResponse;
import swp391.fa25.saleElectricVehicle.service.CloudinaryService;
import swp391.fa25.saleElectricVehicle.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    StoreService storeService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StoreDto>> createStore(@RequestBody StoreDto storeDto) {
        StoreDto createdStore = storeService.createStore(storeDto);
        ApiResponse<StoreDto> response = ApiResponse.<StoreDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Store created successfully")
                .data(createdStore)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Upload ảnh của store
    @PostMapping(
            value = "/{storeId}/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<String>> uploadImageStore(
            @PathVariable int storeId,
            @Parameter(description = "Hình ảnh cửa hàng", required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file) {
        storeService.getStoreById(storeId);
        String fileUrl = cloudinaryService.uploadFile(file, "stores");
        storeService.addStoreImagePath(storeId, fileUrl);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("File uploaded successfully")
                .data(fileUrl)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storeName}")
    public ResponseEntity<ApiResponse<List<StoreDto>>> getStoreByName(@PathVariable String storeName) {
        List<StoreDto> stores = storeService.getStoreByNameContaining(storeName);
        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Stores retrieved successfully")
                .data(stores)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StoreDto>>> getAllStores() {
        List<StoreDto> stores = storeService.getAllStores();
        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
                .code(HttpStatus.OK.value())
                .message("All stores retrieved successfully")
                .data(stores)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<StoreDto>>> getAllActiveStores() {
        List<StoreDto> stores = storeService.getAllActiveStores();
        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Active stores retrieved successfully")
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
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<ApiResponse<List<StoreMonthlyRevenueResponse>>> getMonthlyRevenueForAllStores() {
        List<StoreMonthlyRevenueResponse> revenues = storeService.getMonthlyRevenueForAllStores();
        ApiResponse<List<StoreMonthlyRevenueResponse>> response = ApiResponse.<List<StoreMonthlyRevenueResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Monthly revenue for all stores retrieved successfully")
                .data(revenues)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Get stores by status
//    @GetMapping("/status/{status}")
//    public ResponseEntity<ApiResponse<List<StoreDto>>> getStoresByStatus(@PathVariable String status) {
//        List<StoreDto> stores = storeService.getAllActiveStores();
//        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Stores by status retrieved successfully")
//                .data(stores)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    // BUSINESS - Get stores by province
//    @GetMapping("/province/{provinceName}")
//    public ResponseEntity<ApiResponse<List<StoreDto>>> getStoresByProvince(@PathVariable String provinceName) {
//        List<StoreDto> stores = storeService.getAllStores().stream()
//                .filter(store -> store.getProvinceName().toLowerCase().contains(provinceName.toLowerCase()))
//                .toList();
//
//        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Stores by province retrieved successfully")
//                .data(stores)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    // BUSINESS - Search stores
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse<List<StoreDto>>> searchStores(
//            @RequestParam(required = false) String storeName,
//            @RequestParam(required = false) String provinceName,
//            @RequestParam(required = false) String ownerName) {
//
//        List<StoreDto> stores = storeService.getAllStores();
//
//        if (storeName != null && !storeName.trim().isEmpty()) {
//            stores = stores.stream()
//                    .filter(s -> s.getStoreName().toLowerCase().contains(storeName.toLowerCase()))
//                    .toList();
//        }
//
//        if (provinceName != null && !provinceName.trim().isEmpty()) {
//            stores = stores.stream()
//                    .filter(s -> s.getProvinceName().toLowerCase().contains(provinceName.toLowerCase()))
//                    .toList();
//        }
//
//        if (ownerName != null && !ownerName.trim().isEmpty()) {
//            stores = stores.stream()
//                    .filter(s -> s.getOwnerName().toLowerCase().contains(ownerName.toLowerCase()))
//                    .toList();
//        }
//
//        ApiResponse<List<StoreDto>> response = ApiResponse.<List<StoreDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Search results retrieved successfully")
//                .data(stores)
//                .build();
//        return ResponseEntity.ok(response);
//    }
}