package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.ColorService;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@CrossOrigin(origins = "*")
public class ColorController {

    @Autowired
    ColorService colorService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ColorDto>> createColor(@RequestBody ColorDto colorDto) {
        ColorDto createdColor = colorService.createColor(colorDto);
        ApiResponse<ColorDto> response = ApiResponse.<ColorDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Color created successfully")
                .data(createdColor)
                .build();
//        return ResponseEntity.ok(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ColorDto>> getColorById(@PathVariable int id) {
        ColorDto colorDto = colorService.getColorById(id); // Cần implement
        ApiResponse<ColorDto> response = ApiResponse.<ColorDto>builder()
                .code(HttpStatus.OK.value())
                .message("Color retrieved successfully")
                .data(colorDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}}")
    public ResponseEntity<ApiResponse<List<ColorDto>>> getColorByName(@PathVariable String name) {
        List<ColorDto> colorDto = colorService.getColorByName(name);
        ApiResponse<List<ColorDto>> response = ApiResponse.<List<ColorDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Color retrieved successfully")
                .data(colorDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ColorDto>>> getAllColors() {
        List<ColorDto> colorDtos = colorService.getAllColors();
        ApiResponse<List<ColorDto>> response = ApiResponse.<List<ColorDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Colors retrieved successfully")
                .data(colorDtos)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ColorDto>> updateColor(@PathVariable int id, @RequestBody ColorDto colorDto) {
        ColorDto updatedColor = colorService.updateColor(id, colorDto);
        ApiResponse<ColorDto> response = ApiResponse.<ColorDto>builder()
                .code(HttpStatus.OK.value())
                .message("Color updated successfully")
                .data(updatedColor)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteColor(@PathVariable int id) {
        colorService.deleteColor(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Color deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Search colors
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ColorDto>>> searchColors(@RequestParam String keyword) {
        List<ColorDto> colors = colorService.getAllColors().stream()
                .filter(color -> color.getColorName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        ApiResponse<List<ColorDto>> response = ApiResponse.<List<ColorDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Search results retrieved successfully")
                .data(colors)
                .build();
        return ResponseEntity.ok(response);
    }
}
