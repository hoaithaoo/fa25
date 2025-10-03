package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;

import java.util.List;

public interface ModelService {
    ModelDto createModel(ModelDto modelDto);
    ModelDto getModelById(int id);
    List<ModelDto> getAllModels();
    void deleteModelById(int id);
}
