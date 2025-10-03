package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.repository.ModelRepository;
import swp391.fa25.saleElectricVehicle.service.ModelService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelRepository modelRepository;

    @Override
    public ModelDto createModel(ModelDto modelDto) {
        if (modelRepository.existsModelByModelName(modelDto.getModelName())) {
            throw new AppException(ErrorCode.MODEL_EXISTED);
        }

        Model newModel = Model.builder()
                .modelName(modelDto.getModelName())
                .modelYear(modelDto.getModelYear())
                .batteryCapacity(modelDto.getBatteryCapacity())
                .range(modelDto.getRange())
                .powerHp(modelDto.getPowerHp())
                .torqueNm(modelDto.getTorqueNm())
                .acceleration(modelDto.getAcceleration())
                .seatingCapacity(modelDto.getSeatingCapacity())
                .price(modelDto.getPrice())
                .bodyType(modelDto.getBodyType())
                .description(modelDto.getDescription())
                .createAt(LocalDateTime.now())
                .build();

        modelRepository.save(newModel);
        return modelDto;
    }

    @Override
    public ModelDto getModelById(int id) {
        Model model = modelRepository.findById(id).orElse(null);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }
        return ModelDto.builder()
                .modelName(model.getModelName())
                .modelYear(model.getModelYear())
                .batteryCapacity(model.getBatteryCapacity())
                .range(model.getRange())
                .powerHp(model.getPowerHp())
                .torqueNm(model.getTorqueNm())
                .acceleration(model.getAcceleration())
                .seatingCapacity(model.getSeatingCapacity())
                .price(model.getPrice())
                .bodyType(model.getBodyType())
                .description(model.getDescription())
                .build();
    }

    @Override
    public List<ModelDto> getAllModels() {
        List<Model> models = modelRepository.findAll();
        return models.stream().map(model -> ModelDto.builder()
                .modelName(model.getModelName())
                .modelYear(model.getModelYear())
                .batteryCapacity(model.getBatteryCapacity())
                .range(model.getRange())
                .powerHp(model.getPowerHp())
                .torqueNm(model.getTorqueNm())
                .acceleration(model.getAcceleration())
                .seatingCapacity(model.getSeatingCapacity())
                .price(model.getPrice())
                .bodyType(model.getBodyType())
                .description(model.getDescription())
                .build()).toList();
    }

    @Override
    public void deleteModelById(int id) {
        Model model = modelRepository.findById(id).orElse(null);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }
        modelRepository.delete(model);
    }
}
