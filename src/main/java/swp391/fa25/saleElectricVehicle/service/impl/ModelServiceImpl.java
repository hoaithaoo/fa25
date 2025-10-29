package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.repository.ModelRepository;
import swp391.fa25.saleElectricVehicle.service.ModelService;

import java.math.BigDecimal;
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

        if (modelDto.getModelYear() <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (modelDto.getBatteryCapacity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (modelDto.getRange().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (modelDto.getPowerHp().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (modelDto.getTorqueNm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (modelDto.getAcceleration().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (modelDto.getSeatingCapacity() <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (modelDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
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
        return mapToDto(newModel);
    }

    // dùng cho truy vấn model theo name
    @Override
    public ModelDto getModelByName(String name) {
        Model model = modelRepository.findByModelName(name);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }
        return mapToDto(model);
    }

    // dùng để add model vào store stock
    @Override
    public Model getModelEntityById(int modelId) {
        Model model = modelRepository.findById(modelId).orElse(null);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }
        return model;
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

    @Override
    public ModelDto updateModel(int id, ModelDto modelDto) {
        Model existingModel = modelRepository.findById(id).orElse(null);
        if (existingModel == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }
        if (modelDto.getModelName() != null
                && !modelDto.getModelName().trim().isEmpty()
                && !modelDto.getModelName().equals(existingModel.getModelName())
                && modelRepository.existsModelByModelName(modelDto.getModelName())) {
            throw new AppException(ErrorCode.MODEL_EXISTED);
        }
        existingModel.setModelName(modelDto.getModelName());

        if (modelDto.getModelYear() <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setModelYear(modelDto.getModelYear());

        if (modelDto.getBatteryCapacity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setBatteryCapacity(modelDto.getBatteryCapacity());

        if (modelDto.getRange().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setRange(modelDto.getRange());

        if (modelDto.getPowerHp().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setPowerHp(modelDto.getPowerHp());

        if (modelDto.getTorqueNm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setTorqueNm(modelDto.getTorqueNm());

        if (modelDto.getAcceleration().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setAcceleration(modelDto.getAcceleration());

        if (modelDto.getSeatingCapacity() <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setSeatingCapacity(modelDto.getSeatingCapacity());

        if (modelDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        existingModel.setPrice(modelDto.getPrice());

        // body type phải được dropdown
        if (modelDto.getBodyType() != null
                && modelDto.getBodyType().name().trim().isEmpty()
                && !modelDto.getBodyType().equals(existingModel.getBodyType())) {
            existingModel.setBodyType(modelDto.getBodyType());
        }

        if (modelDto.getDescription() != null
                && modelDto.getDescription().trim().isEmpty()
                && !modelDto.getDescription().equals(existingModel.getDescription())) {
            existingModel.setDescription(modelDto.getDescription());
        }

        existingModel.setUpdatedAt(LocalDateTime.now());

        modelRepository.save(existingModel);
        return mapToDto(existingModel);
    }

    private ModelDto mapToDto(Model model) {
        return ModelDto.builder()
                .modelId(model.getModelId())
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