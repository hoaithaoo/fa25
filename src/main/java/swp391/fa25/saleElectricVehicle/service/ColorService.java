package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Color;
import swp391.fa25.saleElectricVehicle.payload.dto.ColorDto;

import java.util.List;

public interface ColorService {
    ColorDto createColor(ColorDto colorDto);
    ColorDto getColorById(int colorId);
    List<ColorDto> getColorByNameContaining(String colorName);
    Color getColorEntityById(int colorId);
    List<ColorDto> getAllColors();
    ColorDto updateColor(int colorId, ColorDto colorDto);
    void deleteColor(int colorId);
}
