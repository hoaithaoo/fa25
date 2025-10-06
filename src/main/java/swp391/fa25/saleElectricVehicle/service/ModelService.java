package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.payload.request.model.CreateModelRequest;

import java.util.List;

public interface ModelService {
    ModelDto createModel(CreateModelRequest modelDto);
    ModelDto getModelByName(String name);
    List<ModelDto> getAllModels();
    void deleteModelById(int id);
//    ModelDto updateModel(int id, ModelDto modelDto);
}
