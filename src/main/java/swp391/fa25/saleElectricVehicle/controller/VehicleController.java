package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.payload.dto.VehicleDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping(value = "/import/{transactionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadFile(@RequestParam("file") MultipartFile file,
                                                                    @PathVariable int transactionId) {
//        String message = "";
        try {
            List<VehicleDto> vehicles = vehicleService.importVehicles(file, transactionId);
            ApiResponse<List<VehicleDto>> response = ApiResponse.<List<VehicleDto>>builder()
                    .code(HttpStatus.CREATED.value())
                    .data(vehicles)
                    .message("Nhập dữ liệu thành công! Đã thêm danh sách xe vào hệ thống.")
                    .build();

//            message = "Nhập dữ liệu thành công! Đã thêm danh sách xe vào hệ thống.";
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
//            message = "Không thể nhập dữ liệu: " + e.getMessage();
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .code(HttpStatus.CREATED.value())
                    .data(null)
                    .message("Không thể nhập dữ liệu: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleDto>> getVehicleById(@PathVariable long vehicleId) {
        VehicleDto vehicleDto = vehicleService.getVehicleById(vehicleId);
        ApiResponse<VehicleDto> response = ApiResponse.<VehicleDto>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy thông tin xe thành công")
                .data(vehicleDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/inventory/{inventoryId}")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getVehicleByInventoryId(@PathVariable int inventoryId) {
        List<VehicleDto> vehicleDtos = vehicleService.getVehiclesByInventoryTransaction(inventoryId);
        ApiResponse<List<VehicleDto>> response = ApiResponse.<List<VehicleDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách xe từ giao dịch tồn kho thành công")
                .data(vehicleDtos)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update-note/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleDto>> updateVehicleNote(@PathVariable int vehicleId, @RequestParam String note) {
        VehicleDto vehicleDto = vehicleService.updateVehicleNote(vehicleId, note);
        ApiResponse<VehicleDto> response = ApiResponse.<VehicleDto>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật ghi chú xe thành công")
                .data(vehicleDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @GetMapping("/available/model/{modelId}/color/{colorId}")
//    public ResponseEntity<ApiResponse<List<VehicleDto>>> getAvailableVehiclesByModelAndColor(
//            @PathVariable int modelId, @PathVariable int colorId) {
//        List<VehicleDto> vehicleDtos = vehicleService.getAvailableVehiclesByModelAndColor(modelId, colorId);
//        ApiResponse<List<VehicleDto>> response = ApiResponse.<List<VehicleDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Lấy danh sách xe có sẵn theo model và color thành công")
//                .data(vehicleDtos)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

}
