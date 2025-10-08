package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.ModelColor;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.repository.ModelColorRepository;
import swp391.fa25.saleElectricVehicle.service.ModelColorService;

import java.util.List;

@Service
public class ModelColorServiceImpl implements ModelColorService {

    @Autowired
    ModelColorRepository modelColorRepository;


    @Override
    public ModelColorDto getModelColorById(int id) {
        ModelColor modelColor = modelColorRepository.findById(id).orElse(null);
        if (modelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        return ModelColorDto.builder()
                .modelId(modelColor.getModel().getModelId())
                .colorId(modelColor.getColor().getColorId())
                .build();
    }

    @Override
    public List<ModelDto> getModelsByColorId(int colorId) {
        List<ModelColor> modelColors = modelColorRepository.findByColor_ColorId(colorId);
        if (modelColors.isEmpty()) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        return modelColors.stream()
                .map(mc -> ModelDto.builder()
                        .modelId(mc.getModel().getModelId())
                        .modelName(mc.getModel().getModelName())
                        .modelYear(mc.getModel().getModelYear())
                        .batteryCapacity(mc.getModel().getBatteryCapacity())
                        .range(mc.getModel().getRange())
                        .powerHp(mc.getModel().getPowerHp())
                        .torqueNm(mc.getModel().getTorqueNm())
                        .acceleration(mc.getModel().getAcceleration())
                        .seatingCapacity(mc.getModel().getSeatingCapacity())
                        .price(mc.getModel().getPrice())
                        .bodyType(mc.getModel().getBodyType())
                        .description(mc.getModel().getDescription())
                        .build())
                .toList();
    }

    @Override
    public List<ColorDto> getColorsByModelId(int modelId) {
        List<ModelColor> modelColors = modelColorRepository.findByModel_ModelId(modelId);
        if (modelColors.isEmpty()) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        return modelColors.stream()
                .map(mc -> ColorDto.builder()
                        .colorId(mc.getColor().getColorId())
                        .colorName(mc.getColor().getColorName())
                        .build())
                .toList();
    }


}
