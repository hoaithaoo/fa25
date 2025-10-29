package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Color;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.entity.ModelColor;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ModelColorDto;
import swp391.fa25.saleElectricVehicle.payload.request.model.CreateModelColorRequest;
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
    ModelService modelService;

    @Autowired
    ColorService colorService;

    @Override
    public ModelColorDto createModelColor(CreateModelColorRequest request) {
        Model model = modelService.getModelEntityById(request.getModelId());
        Color color = colorService.getColorEntityById(request.getColorId());

        // Check đã tồn tại combination này chưa
        if (modelColorRepository.existsByModel_ModelIdAndColor_ColorId(request.getModelId(), request.getColorId())) {
            throw new AppException(ErrorCode.MODEL_COLOR_EXISTED);
        }

        ModelColor newModelColor = modelColorRepository.save(ModelColor.builder()
                .model(model)
                .color(color)
                .imagePath(request.getImagePath())
                .build());

        return mapToDto(newModelColor);
    }

    @Override
    public ModelColorDto getModelColorById(int id) {
        ModelColor modelColor = modelColorRepository.findById(id).orElse(null);
        if (modelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        return mapToDto(modelColor);
    }

    @Override
    public ModelColor getModelColorEntityByModelIdAndColorId(int modelId, int colorId) {
        ModelColor modelColor = modelColorRepository.findByModel_ModelIdAndColor_ColorId(modelId, colorId);
        if (modelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        return modelColor;
    }

    @Override
    public List<ModelColorDto> getAllModelColors() {
        List<ModelColor> modelColors = modelColorRepository.findAll();
        return modelColors.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ModelColorDto> getModelColorsByModelId(int modelId) {
        List<ModelColor> modelColors = modelColorRepository.findByModel_ModelId(modelId);
        return modelColors.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ModelColorDto> getModelColorsByColorId(int colorId) {
        List<ModelColor> modelColors = modelColorRepository.findByColor_ColorId(colorId);
        return modelColors.stream().map(this::mapToDto).toList();
    }

    @Override
    public ModelColorDto updateModelColor(int id, ModelColorDto modelColorDto) {
        ModelColor existingModelColor = modelColorRepository.findById(id).orElse(null);
        if (existingModelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }

        Model model = modelService.getModelEntityById(modelColorDto.getModelId());
        Color color = colorService.getColorEntityById(modelColorDto.getColorId());

        ModelColor updateModelColor = modelColorRepository.findByModel_ModelIdAndColor_ColorId(model.getModelId(), color.getColorId());
        if (updateModelColor != null && updateModelColor.getModelColorId() != id) {
            throw new AppException(ErrorCode.MODEL_COLOR_EXISTED);
        }
        existingModelColor.setModel(model);
        existingModelColor.setColor(color);

        if (modelColorDto.getImagePath() != null && !modelColorDto.getImagePath().trim().isEmpty()) {
            existingModelColor.setImagePath(modelColorDto.getImagePath());
        }

//        // Check model mới bằng TÊN
//        if (modelColorDto.getModelName() != null && !modelColorDto.getModelName().trim().isEmpty()) {
//            Model model = modelRepository.findByModelName(modelColorDto.getModelName()); // ← Cần check ModelRepository có method này không
//            if (model == null) {
//                throw new AppException(ErrorCode.MODEL_NOT_FOUND);
//            }
//            existingModelColor.setModel(model);
//        }

//        // Check color mới bằng TÊN - SỬA ĐÂY
//        if (modelColorDto.getColorName() != null && !modelColorDto.getColorName().trim().isEmpty()) {
//            Color color = colorRepository.findColorByColorName(modelColorDto.getColorName()); // ← SỬA thêm chữ "Color"
//            if (color == null) {
//                throw new AppException(ErrorCode.COLOR_NOT_EXIST);
//            }
//            existingModelColor.setColor(color);
//        }

        modelColorRepository.save(existingModelColor);
        return mapToDto(existingModelColor);
    }

    @Override
    public void deleteModelColor(int id) {
        ModelColor modelColor = modelColorRepository.findById(id).orElse(null);
        if (modelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        modelColorRepository.delete(modelColor);
    }

    // Thêm method này vào ModelColorServiceImpl
    @Override
    public ModelColor getModelColor(int modelId, int colorId) {
        // Tìm trong repository bằng cách lấy tất cả rồi filter
        List<ModelColor> allModelColors = modelColorRepository.findAll();

        ModelColor modelColor = allModelColors.stream()
                .filter(mc -> mc.getModel().getModelId() == modelId &&
                        mc.getColor().getColorId() == colorId)
                .findFirst()
                .orElse(null);

        if (modelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }

        return modelColor;
    }

    private ModelColorDto mapToDto(ModelColor modelColor) {
        return ModelColorDto.builder()
                .modelColorId(modelColor.getModelColorId())
                .modelId(modelColor.getModel().getModelId())
                .modelName(modelColor.getModel().getModelName())
                .colorId(modelColor.getColor().getColorId())
                .colorName(modelColor.getColor().getColorName())
                .colorCode(modelColor.getColor().getColorCode())
                .imagePath(modelColor.getImagePath())
                .build();
    }
}
