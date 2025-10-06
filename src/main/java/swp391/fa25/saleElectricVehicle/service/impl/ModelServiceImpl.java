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

    @Override
    public ModelDto updateModel(int id, ModelDto modelDto) {
        // Bước 1: Tìm model cần update
        Model existingModel = modelRepository.findById(id).orElse(null);
        if (existingModel == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }

        // Bước 2: Kiểm tra tên model có bị trùng không (nếu đổi tên)
        if (!existingModel.getModelName().equals(modelDto.getModelName()) &&
                modelRepository.existsModelByModelName(modelDto.getModelName())) {
            throw new AppException(ErrorCode.MODEL_EXISTED);
        }

        // Bước 3: Update các fields
        existingModel.setModelName(modelDto.getModelName());
        existingModel.setModelYear(modelDto.getModelYear());
        existingModel.setBatteryCapacity(modelDto.getBatteryCapacity());
        existingModel.setRange(modelDto.getRange());
        existingModel.setPowerHp(modelDto.getPowerHp());
        existingModel.setTorqueNm(modelDto.getTorqueNm());
        existingModel.setAcceleration(modelDto.getAcceleration());
        existingModel.setSeatingCapacity(modelDto.getSeatingCapacity());
        existingModel.setPrice(modelDto.getPrice());
        existingModel.setBodyType(modelDto.getBodyType());
        existingModel.setDescription(modelDto.getDescription());
        existingModel.setUpdatedAt(LocalDateTime.now()); // ← Dùng đúng field name

        // Bước 4: Save vào database
        Model updatedModel = modelRepository.save(existingModel);

        // Bước 5: Convert Entity → DTO và return
        return ModelDto.builder()
                .modelId(updatedModel.getModelId()) // ← Thêm modelId
                .modelName(updatedModel.getModelName())
                .modelYear(updatedModel.getModelYear())
                .batteryCapacity(updatedModel.getBatteryCapacity())
                .range(updatedModel.getRange())
                .powerHp(updatedModel.getPowerHp())
                .torqueNm(updatedModel.getTorqueNm())
                .acceleration(updatedModel.getAcceleration())
                .seatingCapacity(updatedModel.getSeatingCapacity())
                .price(updatedModel.getPrice())
                .bodyType(updatedModel.getBodyType())
                .description(updatedModel.getDescription())
                .build();
    }
}
