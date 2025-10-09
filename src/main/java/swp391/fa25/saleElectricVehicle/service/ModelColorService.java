package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;

import java.util.List;

public interface ModelColorService {
    ModelColorDto createModelColor(String modelName, String colorName);
    ModelColorDto getModelColor(int modelId, int colorId);
    List<ModelDto> getModelsByColorName(String colorName);
    List<ColorDto> getColorsByModelName(String modelName);
}
