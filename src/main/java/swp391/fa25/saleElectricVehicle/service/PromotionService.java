package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Promotion;
import swp391.fa25.saleElectricVehicle.payload.dto.PromotionDto;

import java.util.List;

public interface PromotionService {
    PromotionDto createPromotion(PromotionDto promotionDto);

    List<PromotionDto> getPromotionByName(String promotionName);

    List<PromotionDto> getAllPromotions();

    Promotion getPromotionEntityById(int promotionId);

    List<PromotionDto> getPromotionByModelId(int modelId);

    PromotionDto updatePromotion(int promotionId, PromotionDto promotionDto);

    void deletePromotion(int promotionId);
}
