package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.ModelColorService;

import java.util.List;

@RestController
@RequestMapping("/api/model-colors")
public class ModelColorController {

    @Autowired
    private ModelColorService modelColorService;

    @PostMapping
    public ResponseEntity<ApiResponse<ModelColorDto>> createModelColor(@RequestBody ModelColorDto request) {
        ModelColorDto createdModelColor = modelColorService.createModelColor(request);
        ApiResponse<ModelColorDto> response = ApiResponse.<ModelColorDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Model color created successfully")
                .data(createdModelColor)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelColorDto>> getModelColorById(@PathVariable int id) {
        ModelColorDto modelColorDto = modelColorService.getModelColorById(id);
        ApiResponse<ModelColorDto> response = ApiResponse.<ModelColorDto>builder()
                .code(HttpStatus.OK.value())
                .message("Model color retrieved successfully")
                .data(modelColorDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModelColorDto>>> getAllModelColors() {
        List<ModelColorDto> modelColors = modelColorService.getAllModelColors();
        ApiResponse<List<ModelColorDto>> response = ApiResponse.<List<ModelColorDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Model colors retrieved successfully")
                .data(modelColors)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/model/{modelId}")
    public ResponseEntity<ApiResponse<List<ModelColorDto>>> getModelColorsByModelId(@PathVariable int modelId) {
        List<ModelColorDto> modelColors = modelColorService.getModelColorsByModelId(modelId);
        ApiResponse<List<ModelColorDto>> response = ApiResponse.<List<ModelColorDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Model colors by model retrieved successfully")
                .data(modelColors)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/color/{colorId}")
    public ResponseEntity<ApiResponse<List<ModelColorDto>>> getModelColorsByColorId(@PathVariable int colorId) {
        List<ModelColorDto> modelColors = modelColorService.getModelColorsByColorId(colorId);
        ApiResponse<List<ModelColorDto>> response = ApiResponse.<List<ModelColorDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Model colors by color retrieved successfully")
                .data(modelColors)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelColorDto>> updateModelColor(@PathVariable int id, @RequestBody ModelColorDto modelColorDto) {
        ModelColorDto updatedModelColor = modelColorService.updateModelColor(id, modelColorDto);
        ApiResponse<ModelColorDto> response = ApiResponse.<ModelColorDto>builder()
                .code(HttpStatus.OK.value())
                .message("Model color updated successfully")
                .data(updatedModelColor)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModelColor(@PathVariable int id) {
        modelColorService.deleteModelColor(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Model color deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}