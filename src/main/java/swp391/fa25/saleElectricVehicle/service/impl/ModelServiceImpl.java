package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.payload.request.model.CreateModelRequest;
import swp391.fa25.saleElectricVehicle.repository.ModelRepository;
import swp391.fa25.saleElectricVehicle.service.ModelService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelRepository modelRepository;

    @Override
    public ModelDto createModel(CreateModelRequest creatModelRequest) {
        if (modelRepository.existsModelByModelName(creatModelRequest.getModelName())) {
            throw new AppException(ErrorCode.MODEL_EXISTED);
        }

        Model newModel = Model.builder()
                .modelName(creatModelRequest.getModelName())
                .modelYear(creatModelRequest.getModelYear())
                .batteryCapacity(creatModelRequest.getBatteryCapacity())
                .range(creatModelRequest.getRange())
                .powerHp(creatModelRequest.getPowerHp())
                .torqueNm(creatModelRequest.getTorqueNm())
                .acceleration(creatModelRequest.getAcceleration())
                .seatingCapacity(creatModelRequest.getSeatingCapacity())
                .price(creatModelRequest.getPrice())
                .bodyType(creatModelRequest.getBodyType())
                .description(creatModelRequest.getDescription())
                .createAt(LocalDateTime.now())
                .build();

        modelRepository.save(newModel);
        return mapToDto(newModel);
    }

    @Override
    public ModelDto getModelByName(String name) {
        Model model = modelRepository.findByModelName(name);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }
        return mapToDto(model);
    }

    @Override
    public List<ModelDto> getAllModels() {
        List<Model> models = modelRepository.findAll();
        return models.stream().map(this::mapToDto).toList();
    }

    @Override
    public void deleteModelById(int id) {
        Model model = modelRepository.findById(id).orElse(null);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }
        modelRepository.delete(model);
    }

    private ModelDto mapToDto(Model model) {
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
}
