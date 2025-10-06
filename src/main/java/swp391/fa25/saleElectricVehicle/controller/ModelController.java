package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.payload.request.model.CreateModelRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.ModelService;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ModelDto>> createModel(@RequestBody CreateModelRequest modelDto) {
        ModelDto createdModel = modelService.createModel(modelDto);
        ApiResponse<ModelDto> response = ApiResponse.<ModelDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create model successfully")
                .data(createdModel)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<ModelDto>> getModelById(@PathVariable String name) {
        ModelDto modelDto = modelService.getModelByName(name);
        ApiResponse<ModelDto> response = ApiResponse.<ModelDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get model successfully")
                .data(modelDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ModelDto>>> getAllModels() {
        List<ModelDto> models = modelService.getAllModels();
        ApiResponse<List<ModelDto>> response = ApiResponse.<List<ModelDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Model fetched successfully")
                .data(models)
                .build();
        return ResponseEntity.ok(response);
    }

    //PUT model here

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModel(@PathVariable int id) {
        modelService.deleteModelById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete model successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
