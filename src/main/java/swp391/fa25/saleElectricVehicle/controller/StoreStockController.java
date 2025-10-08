package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.StoreStockService;

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
}
