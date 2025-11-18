package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.TestDriveConfigDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.TestDriveConfigService;

@RestController
@RequestMapping("/test-drive-configs")
public class TestDriveConfigController {

    @Autowired
    private TestDriveConfigService testDriveConfigService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TestDriveConfigDto>> createTestDriveConfig(
            @Valid @RequestBody TestDriveConfigDto dto) {
        TestDriveConfigDto created = testDriveConfigService.createTestDriveConfig(dto);
        ApiResponse<TestDriveConfigDto> response = ApiResponse.<TestDriveConfigDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Test drive config created successfully")
                .data(created)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<TestDriveConfigDto>> getTestDriveConfig() {
        TestDriveConfigDto dto = testDriveConfigService.getTestDriveConfig();
        ApiResponse<TestDriveConfigDto> response = ApiResponse.<TestDriveConfigDto>builder()
                .code(HttpStatus.OK.value())
                .message("Test drive config fetched successfully")
                .data(dto)
                .build();
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/store/{storeId}")
//    public ResponseEntity<ApiResponse<TestDriveConfigDto>> getTestDriveConfigByStoreId(
//            @PathVariable Integer storeId) {
//        TestDriveConfigDto dto = testDriveConfigService.getTestDriveConfigByStoreId(storeId);
//        ApiResponse<TestDriveConfigDto> response = ApiResponse.<TestDriveConfigDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Test drive config fetched successfully by store")
//                .data(dto)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<ApiResponse<List<TestDriveConfigDto>>> getAllTestDriveConfigs() {
//        List<TestDriveConfigDto> dtos = testDriveConfigService.getAllTestDriveConfigs();
//        ApiResponse<List<TestDriveConfigDto>> response = ApiResponse.<List<TestDriveConfigDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("All test drive configs fetched successfully")
//                .data(dtos)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    @PutMapping("/update/{configId}")
    public ResponseEntity<ApiResponse<TestDriveConfigDto>> updateTestDriveConfig(
            @PathVariable Integer configId,
            @RequestBody TestDriveConfigDto dto) {
        TestDriveConfigDto updated = testDriveConfigService.updateTestDriveConfig(configId, dto);
        ApiResponse<TestDriveConfigDto> response = ApiResponse.<TestDriveConfigDto>builder()
                .code(HttpStatus.OK.value())
                .message("Test drive config updated successfully")
                .data(updated)
                .build();
        return ResponseEntity.ok(response);
    }

//    @PutMapping("/update/store/{storeId}")
//    public ResponseEntity<ApiResponse<TestDriveConfigDto>> updateTestDriveConfig(
//            @PathVariable Integer storeId,
//            @RequestBody TestDriveConfigDto dto) {
//        TestDriveConfigDto updated = testDriveConfigService.updateTestDriveConfigByStoreId(storeId, dto);
//        ApiResponse<TestDriveConfigDto> response = ApiResponse.<TestDriveConfigDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Test drive config updated successfully by store")
//                .data(updated)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/delete/{configId}")
    public ResponseEntity<ApiResponse<Void>> deleteTestDriveConfig(@PathVariable Integer configId) {
        testDriveConfigService.deleteTestDriveConfig(configId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Test drive config deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
