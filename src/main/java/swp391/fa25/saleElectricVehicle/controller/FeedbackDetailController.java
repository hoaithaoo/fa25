package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDetailDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.FeedbackDetailService;

import java.util.List;

@RestController
@RequestMapping("/feedback-details")
public class FeedbackDetailController {

    @Autowired
    private FeedbackDetailService feedbackDetailService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FeedbackDetailDto>> createFeedbackDetail(
            @RequestBody FeedbackDetailDto dto) {
        FeedbackDetailDto created = feedbackDetailService.createFeedbackDetail(dto);
        ApiResponse<FeedbackDetailDto> response = ApiResponse.<FeedbackDetailDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Feedback detail created successfully")
                .data(created)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{feedbackDetailId}")
    public ResponseEntity<ApiResponse<FeedbackDetailDto>> getFeedbackDetailById(
            @PathVariable int feedbackDetailId) {
        FeedbackDetailDto dto = feedbackDetailService.getFeedbackDetailById(feedbackDetailId);
        ApiResponse<FeedbackDetailDto> response = ApiResponse.<FeedbackDetailDto>builder()
                .code(HttpStatus.OK.value())
                .message("Feedback detail fetched successfully")
                .data(dto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FeedbackDetailDto>>> getAllFeedbackDetails() {
        List<FeedbackDetailDto> dtos = feedbackDetailService.getAllFeedbackDetails();
        ApiResponse<List<FeedbackDetailDto>> response = ApiResponse.<List<FeedbackDetailDto>>builder()
                .code(HttpStatus.OK.value())
                .message("All feedback details fetched successfully")
                .data(dtos)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feedback/{feedbackId}")
    public ResponseEntity<ApiResponse<List<FeedbackDetailDto>>> getFeedbackDetailsByFeedback(
            @PathVariable int feedbackId) {
        List<FeedbackDetailDto> dtos = feedbackDetailService.getFeedbackDetailsByFeedback(feedbackId);
        ApiResponse<List<FeedbackDetailDto>> response = ApiResponse.<List<FeedbackDetailDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Feedback details by feedback fetched successfully")
                .data(dtos)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{feedbackDetailId}")
    public ResponseEntity<ApiResponse<FeedbackDetailDto>> updateFeedbackDetail(
            @PathVariable int feedbackDetailId,
            @Valid @RequestBody FeedbackDetailDto dto) {
        FeedbackDetailDto updated = feedbackDetailService.updateFeedbackDetail(feedbackDetailId, dto);
        ApiResponse<FeedbackDetailDto> response = ApiResponse.<FeedbackDetailDto>builder()
                .code(HttpStatus.OK.value())
                .message("Feedback detail updated successfully")
                .data(updated)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{feedbackDetailId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedbackDetail(@PathVariable int feedbackDetailId) {
        feedbackDetailService.deleteFeedbackDetail(feedbackDetailId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Feedback detail deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
