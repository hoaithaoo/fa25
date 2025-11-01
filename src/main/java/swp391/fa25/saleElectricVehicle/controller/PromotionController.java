package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.PromotionDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.PromotionService;

import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PromotionDto>> createPromotion(@RequestBody PromotionDto promotionDto) {
        PromotionDto createdPromotion = promotionService.createPromotion(promotionDto);
        ApiResponse<PromotionDto> response = ApiResponse.<PromotionDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Promotion created successfully")
                .data(createdPromotion)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getPromotionByName(@PathVariable String name) {
        List<PromotionDto> promotionDto = promotionService.getPromotionByName(name);
        ApiResponse<List<PromotionDto>> response = ApiResponse.<List<PromotionDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Promotion retrieved successfully")
                .data(promotionDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/model/{modelId}")
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getPromotionByModelId(@PathVariable int modelId) {
        List<PromotionDto> promotionDto = promotionService.getPromotionByModelId(modelId);
        ApiResponse<List<PromotionDto>> response = ApiResponse.<List<PromotionDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Get promotion by model successfully")
                .data(promotionDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getAllPromotions() {
        List<PromotionDto> promotions = promotionService.getAllPromotions();
        ApiResponse<List<PromotionDto>> response = ApiResponse.<List<PromotionDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Promotions retrieved successfully")
                .data(promotions)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionDto>> updatePromotion(@PathVariable("id") int promotionId, @RequestBody PromotionDto promotionDto) {
        PromotionDto updatedPromotion = promotionService.updatePromotion(promotionId, promotionDto);
        ApiResponse<PromotionDto> response = ApiResponse.<PromotionDto>builder()
                .code(HttpStatus.OK.value())
                .message("Promotion updated successfully")
                .data(updatedPromotion)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable("id") int promotionId) {
        promotionService.deletePromotion(promotionId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Promotion deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
