package swp391.fa25.saleElectricVehicle.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;
import swp391.fa25.saleElectricVehicle.payload.dto.PaymentInfoDto;
import swp391.fa25.saleElectricVehicle.payload.request.inventory.CreateInventoryTransactionRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.CloudinaryService;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionService;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory-transactions")
public class InventoryTransactionController {

    @Autowired
    private InventoryTransactionService inventoryTransactionService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> createInventoryTransaction(
            @Valid @RequestBody CreateInventoryTransactionRequest request) {

        InventoryTransactionDto created = inventoryTransactionService.createInventoryTransaction(request);

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

//    @PutMapping("/update/{inventoryId}")
//    public ResponseEntity<ApiResponse<InventoryTransactionDto>> updateInventoryTransaction(
//            @PathVariable int inventoryId,
//            @Valid @RequestBody InventoryTransactionDto dto) {
//
//        InventoryTransactionDto updated =
//                inventoryTransactionService.updateInventoryTransaction(inventoryId, dto);
//
//        ApiResponse<InventoryTransactionDto> response =
//                ApiResponse.<InventoryTransactionDto>builder()
//                        .code(HttpStatus.OK.value())
//                        .message("Inventory transaction updated successfully")
//                        .data(updated)
//                        .build();
//
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/delete/{inventoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteInventoryTransaction(@PathVariable int inventoryId) {

        inventoryTransactionService.deleteInventoryTransaction(inventoryId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Inventory transaction deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/accept/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> acceptRequest(
            @PathVariable int inventoryId) {

        InventoryTransactionDto accepted = inventoryTransactionService.acceptRequest(inventoryId);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Request accepted successfully")
                .data(accepted)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/reject/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> rejectRequest(
            @PathVariable int inventoryId) {

        InventoryTransactionDto rejected = inventoryTransactionService.rejectRequest(inventoryId);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Request rejected successfully")
                .data(rejected)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/start-shipping/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> startShipping(
            @PathVariable int inventoryId) {

        InventoryTransactionDto started = inventoryTransactionService.startShipping(inventoryId);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Shipping started successfully")
                .data(started)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/confirm-delivery/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> confirmDelivery(
            @PathVariable int inventoryId) {

        InventoryTransactionDto confirmed = inventoryTransactionService.confirmDelivery(inventoryId);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Delivery confirmed successfully. Stock updated.")
                .data(confirmed)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/{inventoryId}/upload-receipt",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> uploadReceipt(
            @PathVariable int inventoryId,
            @Parameter(description = "Biên lai thanh toán", required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file) {

        // Upload file lên Cloudinary
        String fileUrl = cloudinaryService.uploadFile(file, "inventory-receipts");
        
        // Cập nhật transaction với imageUrl và status
        InventoryTransactionDto updated = inventoryTransactionService.uploadReceipt(inventoryId, fileUrl);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Biên lai đã được upload thành công")
                .data(updated)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{inventoryId}/payment-info")
    public ResponseEntity<ApiResponse<PaymentInfoDto>> getPaymentInfo(
            @PathVariable int inventoryId) {

        PaymentInfoDto paymentInfo = inventoryTransactionService.getPaymentInfo(inventoryId);

        ApiResponse<PaymentInfoDto> response = ApiResponse.<PaymentInfoDto>builder()
                .code(HttpStatus.OK.value())
                .message("Thông tin thanh toán đã được lấy thành công")
                .data(paymentInfo)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{inventoryId}/confirm-payment")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> confirmPayment(
            @PathVariable int inventoryId) {

        InventoryTransactionDto confirmed = inventoryTransactionService.confirmPayment(inventoryId);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Thanh toán đã được xác nhận thành công")
                .data(confirmed)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{inventoryId}/cancel")
    public ResponseEntity<ApiResponse<InventoryTransactionDto>> cancelRequest(
            @PathVariable int inventoryId) {

        InventoryTransactionDto cancelled = inventoryTransactionService.cancelRequest(inventoryId);

        ApiResponse<InventoryTransactionDto> response = ApiResponse.<InventoryTransactionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Request đã được hủy thành công")
                .data(cancelled)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<String>>> getInventoryTransactionStatus() {
        List<String> statuses = Arrays.stream(InventoryTransactionStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Inventory transaction status retrieved successfully")
                .data(statuses)
                .build();
        return ResponseEntity.ok(response);
    }
}
