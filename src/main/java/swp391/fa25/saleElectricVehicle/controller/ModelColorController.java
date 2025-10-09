package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.ModelColorService;

@RestController
@RequestMapping("/api/model-colors")
public class ModelColorController {

    @Autowired
    private ModelColorService modelColorService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ModelColorDto>> createModel(@RequestBody ModelColorDto modelColorDto) {
        ModelColorDto createdModel = modelColorService.createModelColor(modelColorDto.getModelName(), modelColorDto.getColorName());
        ApiResponse<ModelColorDto> response = ApiResponse.<ModelColorDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Model created successfully")
                .data(createdModel)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(response);
    }
}
