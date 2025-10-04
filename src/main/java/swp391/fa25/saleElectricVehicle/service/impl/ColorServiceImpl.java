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
        Color color = Color.builder()
                .colorName(colorDto.getColorName())
                .build();
        colorRepository.save(color);
        return colorDto;
    }

    @Override
    public List<ColorDto> getColorByName(String colorName) {
        List<Color> colors = colorRepository.findColorsByColorName(colorName);
        if (colors.isEmpty()) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }
        return colors.stream().map(color -> ColorDto.builder()
                .colorId(color.getColorId())
                .colorName(color.getColorName())
                .build()).toList();
    }

    @Override
    public List<ColorDto> getAllColors() {
        List<Color> colors = colorRepository.findAll();
        return colors.stream().map(color -> ColorDto.builder()
                .colorId(color.getColorId())
                .colorName(color.getColorName())
                .build()).toList();
    }

    @Override
    public ColorDto updateColor(int colorId, ColorDto colorDto) {
        Color color = colorRepository.findById(colorId).orElse(null);
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }
        if (colorRepository.existsColorByColorName(colorDto.getColorName())) {
            throw new AppException(ErrorCode.COLOR_EXISTED);
        } else {
            color.setColorName(colorDto.getColorName());
        }
        colorRepository.save(color);
        return colorDto;
    }

    @Override
    public void deleteColor(int colorId) {
        Color color = colorRepository.findById(colorId).orElse(null);
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }
        colorRepository.delete(color);
    }
}
