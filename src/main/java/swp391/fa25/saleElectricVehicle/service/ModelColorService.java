package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;

import java.util.List;

public interface ModelColorService {
    ModelColorDto getModelColorById(int id);
    List<ModelDto> getModelsByColorId(int colorId);
    List<ColorDto> getColorsByModelId(int modelId);
}
