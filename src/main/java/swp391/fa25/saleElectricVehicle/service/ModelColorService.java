package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.ModelColor;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.request.model.CreateModelColorRequest;

import java.util.List;

public interface ModelColorService {
    ModelColorDto createModelColor(CreateModelColorRequest request);
    ModelColorDto addModelColorImagePath(int modelColorId, String imagePath);
    ModelColorDto getModelColorById(int id);
    ModelColor getModelColorEntityByModelIdAndColorId(int modelId, int colorId);
    ModelColorDto getModelColorByModelIdAndColorId(int modelId, int colorId);
    List<ModelColorDto> getAllModelColors();
    List<ModelColorDto> getModelColorsByModelId(int modelId);
    List<ModelColorDto> getModelColorsByColorId(int colorId);
    ModelColorDto updateModelColor(int id, ModelColorDto modelColorDto);
    void deleteModelColor(int id);
    ModelColor getModelColor(int modelId, int colorId);


}
