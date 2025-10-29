package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.CloudinaryService;

//@RestController
//@RequestMapping("/api/files")
//@RequiredArgsConstructor
public class FileController {

//    @Autowired
//    private CloudinaryService cloudinaryService;
//
//    // Upload hợp đồng đã ký
//    @PostMapping("/{contractId}/upload-signed")
//    public ResponseEntity<ApiResponse<String>> uploadSignedContract(
//            @PathVariable int contractId,
//            @RequestParam("file") MultipartFile file) {
//        String fileUrl = cloudinaryService.uploadFile(file, "contracts");
//        ApiResponse<String> response = ApiResponse.<String>builder()
//                .code(HttpStatus.OK.value())
//                .message("File uploaded successfully")
//                .data(fileUrl)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
}
