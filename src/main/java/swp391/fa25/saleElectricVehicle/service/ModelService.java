package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;

import java.util.List;

public interface ModelService {
    ModelDto createModel(ModelDto modelDto);
    ModelDto updateModel(int id, ModelDto modelDto);
//    ModelDto getModelById(int modelId);
    Model getModelEntityById(int modelId);
    ModelDto getModelByName(String name);
    List<ModelDto> getAllModels();
    void deleteModelById(int id);

//    ModelDto updateModel(int id, ModelDto modelDto);
}
