package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Color;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.entity.ModelColor;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelDto;
import swp391.fa25.saleElectricVehicle.repository.ModelColorRepository;
import swp391.fa25.saleElectricVehicle.service.ColorService;
import swp391.fa25.saleElectricVehicle.service.ModelColorService;
import swp391.fa25.saleElectricVehicle.service.ModelService;

import java.util.List;

@Service
public class ModelColorServiceImpl implements ModelColorService {

    @Autowired
    ModelColorRepository modelColorRepository;

    @Autowired
    ColorService colorService;

    @Autowired
    ModelService modelService;

    @Override
    public ModelColorDto createModelColor(String modelName, String colorName) {
        Color color = colorService.getColorEntityByName(colorName);
        Model model = modelService.getModelEntityByName(modelName);

        // check sự tồn tại của model và color trong bảng model_color
        ModelColor existingModelColor = modelColorRepository.findByModel_ModelIdAndColor_ColorId(model.getModelId(), color.getColorId());
        if (existingModelColor != null) {
            throw new AppException(ErrorCode.MODEL_COLOR_EXISTED);
        }

        ModelColor newModelColor = ModelColor.builder()
                .model(model)
                .color(color)
                .build();

        modelColorRepository.save(newModelColor);
        return ModelColorDto.builder()
                .modelName(model.getModelName())
                .colorName(color.getColorName())
                .build();
    }

    // dùng để kiểm tra sự tồn tại của model và color ở store stock
    @Override
    public ModelColorDto getModelColor(int modelId, int colorId) {
        ModelColor modelColor = modelColorRepository.findByModel_ModelIdAndColor_ColorId(modelId, colorId);
        if (modelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        return ModelColorDto.builder()
                .modelName(modelColor.getModel().getModelName())
                .colorName(modelColor.getColor().getColorName())
                .build();

    }

    @Override
    public List<ModelDto> getModelsByColorName(String colorName) {
        ColorDto colorDto = colorService.getColorByName(colorName);
        List<ModelColor> modelColors = modelColorRepository.findByColor_ColorId(colorDto.getColorId());
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
    public List<ColorDto> getColorsByModelName(String modelName) {
        ModelDto modelDto = modelService.getModelByName(modelName);
        List<ModelColor> modelColors = modelColorRepository.findByModel_ModelId(modelDto.getModelId());
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
