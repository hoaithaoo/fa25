package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.PromotionDto;

import java.util.List;

public interface PromotionService {
    PromotionDto createPromotion(PromotionDto promotionDto);

    List<PromotionDto> getPromotionByName(String promotionName);

    List<PromotionDto> getAllPromotions();

    PromotionDto updatePromotion(int promotionId, PromotionDto promotionDto);

    void deletePromotion(int promotionId);
}
