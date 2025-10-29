package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Color;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;
import swp391.fa25.saleElectricVehicle.repository.ColorRepository;
import swp391.fa25.saleElectricVehicle.service.ColorService;

import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {

    @Autowired
    ColorRepository colorRepository;

    @Override
    public ColorDto createColor(ColorDto colorDto) {
        if (colorRepository.existsColorByColorName(colorDto.getColorName())) {
            throw new AppException(ErrorCode.COLOR_EXISTED);
        }

        if (colorRepository.existsByColorCode(colorDto.getColorCode())) {
            throw new AppException(ErrorCode.COLOR_CODE_EXISTED);
        }

        Color color = colorRepository.save(Color.builder()
                .colorName(colorDto.getColorName())
                .colorCode(colorDto.getColorCode())
                .build());
        return mapToDto(color);
    }

    @Override
    public ColorDto getColorById(int colorId) {
        Color color = colorRepository.findById(colorId).orElse(null);
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }

        return mapToDto(color);
    }

    @Override
    public List<ColorDto> getColorByNameContaining(String colorName) {
        List<Color> colors = colorRepository.findColorsByColorNameContaining(colorName);
        if (colors.isEmpty()) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }
        return colors.stream().map(this::mapToDto).toList();
    }

//    // truy vấn color theo name
//    @Override
//    public ColorDto getColorByName(String colorName) {
//        Color color = colorRepository.findColorByColorName(colorName);
//        if (color == null) {
//            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
//        }
//        return mapToDto(color);
//    }

//    // add color vào store stock
//    @Override
//    public Color getColorEntityByName(String colorName) {
//        Color color = colorRepository.findColorByColorName(colorName);
//        if (color == null) {
//            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
//        }
//        return color;
//    }

    // add color vào store stock
    @Override
    public Color getColorEntityById(int colorId) {
        Color color = colorRepository.findById(colorId).orElse(null);
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }
        return color;
    }

    @Override
    public List<ColorDto> getAllColors() {
        List<Color> colors = colorRepository.findAll();
        return colors.stream().map(this::mapToDto).toList();
    }

    @Override
    public ColorDto updateColor(int colorId, ColorDto colorDto) {
        Color color = colorRepository.findById(colorId).orElse(null);
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }
//        if (colorRepository.existsColorByColorName(colorDto.getColorName())) {
//            throw new AppException(ErrorCode.COLOR_EXISTED);
//        } else {
//            color.setColorName(colorDto.getColorName());
//        }
//        colorRepository.save(color);
//        return colorDto;

//        Sửa logic:
//        if (color.getColorName() != null && !color.getColorName().trim().isEmpty()
//                && colorRepository.existsColorByColorName(colorDto.getColorName())) {
//            throw new AppException(ErrorCode.COLOR_EXISTED);
//        }
        // ✅ Sửa lại
        if (!color.getColorName().equals(colorDto.getColorName())
                && colorDto.getColorName() != null
                && !colorDto.getColorName().trim().isEmpty()
                && colorRepository.existsColorByColorName(colorDto.getColorName())) {
            throw new AppException(ErrorCode.COLOR_EXISTED);
        }
        color.setColorName(colorDto.getColorName());

        if (colorDto.getColorCode() != null
                && !colorDto.getColorCode().trim().isEmpty()
                && !color.getColorCode().equals(colorDto.getColorCode())
                && colorRepository.existsByColorCode(colorDto.getColorCode())) {
            throw new AppException(ErrorCode.COLOR_CODE_EXISTED);
        }
        color.setColorCode(colorDto.getColorCode());

        colorRepository.save(color);

        return mapToDto(color);
    }

    @Override
    public void deleteColor(int colorId) {
        Color color = colorRepository.findById(colorId).orElse(null);
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }
        colorRepository.delete(color);
    }

    private ColorDto mapToDto(Color color) {
        return ColorDto.builder()
                .colorId(color.getColorId())
                .colorName(color.getColorName())
                .colorCode(color.getColorCode())
                .build();
    }
}
