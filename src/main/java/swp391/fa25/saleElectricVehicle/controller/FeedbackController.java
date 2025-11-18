package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDto;
import swp391.fa25.saleElectricVehicle.payload.request.feedback.CreateFeedbackRequest; // ✅ THÊM MỚI
import swp391.fa25.saleElectricVehicle.payload.request.feedback.UpdateFeedbackRequest; // ✅ THÊM MỚI
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.FeedbackService;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.FeedbackStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.FeedbackCategory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // ✅ THAY ĐỔI: Dùng CreateFeedbackRequest thay vì FeedbackDto
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FeedbackDto>> createDraftFeedback(
            @RequestBody CreateFeedbackRequest request) { // ✅ Đổi từ FeedbackDto → CreateFeedbackRequest
        FeedbackDto created = feedbackService.createFeedback(request);
        ApiResponse<FeedbackDto> response = ApiResponse.<FeedbackDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Feedback created successfully")
                .data(created)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<FeedbackDto>> getFeedbackById(
            @PathVariable int feedbackId) {
        FeedbackDto dto = feedbackService.getFeedbackById(feedbackId);
        ApiResponse<FeedbackDto> response = ApiResponse.<FeedbackDto>builder()
                .code(HttpStatus.OK.value())
                .message("Feedback fetched successfully")
                .data(dto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getAllFeedbacks() {
        List<FeedbackDto> dtos = feedbackService.getAllFeedbacks();
        ApiResponse<List<FeedbackDto>> response = ApiResponse.<List<FeedbackDto>>builder()
                .code(HttpStatus.OK.value())
                .message("All feedbacks fetched successfully")
                .data(dtos)
                .build();
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/status/{status}")
//    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getFeedbacksByStatus(
//            @PathVariable String status) {
//        List<FeedbackDto> dtos = feedbackService.getFeedbacksByStatus(status);
//        ApiResponse<List<FeedbackDto>> response = ApiResponse.<List<FeedbackDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Feedbacks by status fetched successfully")
//                .data(dtos)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getFeedbacksByOrder(
            @PathVariable int orderId) {
        List<FeedbackDto> dtos = feedbackService.getFeedbacksByOrder(orderId);
        ApiResponse<List<FeedbackDto>> response = ApiResponse.<List<FeedbackDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Feedbacks by order fetched successfully")
                .data(dtos)
                .build();
        return ResponseEntity.ok(response);
    }

    // ✅ THAY ĐỔI: Dùng UpdateFeedbackRequest thay vì FeedbackDto
    @PutMapping("/update/{feedbackId}")
    public ResponseEntity<ApiResponse<FeedbackDto>> updateFeedback(
            @PathVariable int feedbackId,
            @RequestBody UpdateFeedbackRequest request) { // ✅ Đổi từ FeedbackDto → UpdateFeedbackRequest
        FeedbackDto updated = feedbackService.updateFeedback(feedbackId, request);
        ApiResponse<FeedbackDto> response = ApiResponse.<FeedbackDto>builder()
                .code(HttpStatus.OK.value())
                .message("Feedback updated successfully")
                .data(updated)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{feedbackId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable int feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Feedback deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<String>>> getFeedbackStatus() {
        List<String> statuses = Arrays.stream(FeedbackStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Feedback status retrieved successfully")
                .data(statuses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getFeedbackCategories() {
        List<String> categories = Arrays.stream(FeedbackCategory.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Feedback categories retrieved successfully")
                .data(categories)
                .build();
        return ResponseEntity.ok(response);
    }
}
