package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.ModelColorService;
import swp391.fa25.saleElectricVehicle.service.ModelService;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @Autowired
    private ModelColorService modelColorService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ModelDto>> createModel(@RequestBody ModelDto modelDto) {
        ModelDto createdModel = modelService.createModel(modelDto);
        ApiResponse<ModelDto> response = ApiResponse.<ModelDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create model successfully")
                .data(createdModel)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response); //Thay thế return ResponseEntity.ok(response);
        //ResponseEntity.ok(response) mặc định trả về mã trạng thái HTTP 200 (OK)
        //nhưng .code(HttpStatus.CREATED.value()) trả về 201 (Created) nềuu tạo thành công
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<ModelDto>> getModelByName(@PathVariable String name) {
        ModelDto modelDto = modelService.getModelByName(name);
        ApiResponse<ModelDto> response = ApiResponse.<ModelDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get model successfully")
                .data(modelDto)
                .build();
        return ResponseEntity.ok(response);
    }

//    // lay model theo color name
//    @GetMapping("/{colorName}/models")
//    public ResponseEntity<ApiResponse<List<ModelDto>>> getModelsByColor(@PathVariable String colorName) {
//        List<ModelDto> models = modelColorService.getModelsByColorName(colorName);
//        ApiResponse<List<ModelDto>> response = ApiResponse.<List<ModelDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Models fetched successfully")
//                .data(models)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    // lay color theo model name
//    @GetMapping("/{modelName}/colors")
//    public ResponseEntity<ApiResponse<List<ColorDto>>> getColorsByModel(@PathVariable String modelName) {
//        List<ColorDto> colors = modelColorService.getColorsByModelName(modelName);
//        ApiResponse<List<ColorDto>> response = ApiResponse.<List<ColorDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Colors fetched successfully")
//                .data(colors)
//                .build();
//        return ResponseEntity.ok(response);
//    }


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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelDto>> updateModel(@PathVariable int id, @RequestBody ModelDto modelDto) {
        ModelDto updatedModel = modelService.updateModel(id, modelDto);
        ApiResponse<ModelDto> response = ApiResponse.<ModelDto>builder()
                .code(HttpStatus.OK.value())
                .message("Update model successfully")
                .data(updatedModel)
                .build();
        return ResponseEntity.ok(response);
    }

}
