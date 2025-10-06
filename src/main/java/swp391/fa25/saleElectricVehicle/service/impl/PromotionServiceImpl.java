package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.entity.Promotion;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PromotionType;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.PromotionDto;
import swp391.fa25.saleElectricVehicle.repository.ModelRepository;
import swp391.fa25.saleElectricVehicle.repository.PromotionRepository;
import swp391.fa25.saleElectricVehicle.repository.StoreRepository;
import swp391.fa25.saleElectricVehicle.service.PromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    ModelRepository modelRepository;

    @Autowired
    StoreRepository storeRepository;

    @Override
    public PromotionDto createPromotion(PromotionDto promotionDto) {
        Promotion promotion = promotionRepository.findById(promotionDto.getPromotionId()).orElse(null);
        if (promotion != null) {
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }

        Model model = modelRepository.findById(promotionDto.getModelId()).orElse(null);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }

        Store store = storeRepository.findById(promotionDto.getStoreId()).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        if (promotionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        if (promotionDto.getEndDate().isBefore(promotionDto.getStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        Promotion newPromotion = Promotion.builder()
                .promotionName(promotionDto.getPromotionName())
                .description(promotionDto.getDescription())
                .promotionType(promotionDto.getPromotionType())
                .amount(promotionDto.getAmount())
                .startDate(promotionDto.getStartDate())
                .endDate(promotionDto.getEndDate())
                .isActive(promotionDto.isActive())
                .model(model)
                .store(store)
                .createdAt(LocalDateTime.now())
                .build();

        promotionRepository.save(newPromotion);

        return mapToDto(newPromotion);
    }

    @Override
    public List<PromotionDto> getPromotionByName(String promotionName) {
        List<Promotion> promotion = promotionRepository.findByPromotionNameContainingIgnoreCase(promotionName);
        if (promotion.isEmpty()) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }

        return promotion.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<PromotionDto> getAllPromotions() {
        List<Promotion> promotions = promotionRepository.findAll();
        return promotions.stream().map(this::mapToDto).toList();
    }

    @Override
    public PromotionDto updatePromotion(int promotionId, PromotionDto promotionDto) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }

        if (promotionRepository.existsByPromotionNameIgnoreCase(promotion.getPromotionName())) {
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }

        Model model = modelRepository.findById(promotionDto.getModelId()).orElse(null);
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        } else {
            promotion.setModel(model);
        }

        Store store = storeRepository.findById(promotionDto.getStoreId()).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        } else {
            promotion.setStore(store);
        }

        if (promotionDto.getAmount() != null) {
            if (promotionDto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                promotion.setAmount(promotionDto.getAmount());
            } else {
                throw new AppException(ErrorCode.INVALID_AMOUNT);
            }
        }

        if (promotionDto.getStartDate() != null) {
            promotion.setStartDate(promotionDto.getStartDate());
        }

        if (promotionDto.getEndDate() != null) {
            if (!promotionDto.getEndDate().isBefore(promotion.getStartDate())) {
                if (promotionDto.getEndDate().isBefore(LocalDateTime.now())) {
                    throw new AppException(ErrorCode.INVALID_END_DATE_TIME);
                }
                promotion.setEndDate(promotionDto.getEndDate());
            } else {
                throw new AppException(ErrorCode.INVALID_END_DATE);
            }
        }

        if (promotionDto.getPromotionName() != null && !promotionDto.getPromotionName().isEmpty()) {
            promotion.setPromotionName(promotionDto.getPromotionName());

        }

        if (promotionDto.getDescription() != null && !promotionDto.getDescription().isEmpty()) {
            promotion.setDescription(promotionDto.getDescription());
        }

        if (promotionDto.getPromotionType() != null) {
            promotion.setPromotionType(promotionDto.getPromotionType());
        }

        if (promotionDto.isActive() != promotion.isActive()) {
            promotion.setActive(promotionDto.isActive());
        }

        promotion.setUpdatedAt(LocalDateTime.now());

        promotionRepository.save(promotion);

        return promotionDto;
    }

    @Override
    public void deletePromotion(int promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        promotionRepository.delete(promotion);
    }

    private PromotionDto mapToDto(Promotion promotion) {
        return PromotionDto.builder()
                .promotionId(promotion.getPromotionId())
                .promotionName(promotion.getPromotionName())
                .description(promotion.getDescription())
                .promotionType(promotion.getPromotionType())
                .amount(promotion.getAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.isActive())
                .modelId(promotion.getModel().getModelId())
                .storeId(promotion.getStore().getStoreId())
                .build();
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deactivateExpiredPromotions() {
        List<Promotion> activePromotions = promotionRepository.findByIsActiveTrue();
        LocalDateTime now = LocalDateTime.now();
        for (Promotion promotion : activePromotions) {
            if (promotion.getEndDate().isBefore(now)) {
                promotion.setActive(false);
                promotion.setUpdatedAt(now);
                promotionRepository.save(promotion);
            }
        }
    }
}
