package swp391.fa25.saleElectricVehicle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.entity.Promotion;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.PromotionDto;
import swp391.fa25.saleElectricVehicle.repository.PromotionRepository;
import swp391.fa25.saleElectricVehicle.service.ModelService;
import swp391.fa25.saleElectricVehicle.service.PromotionService;
import swp391.fa25.saleElectricVehicle.service.StoreService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    ModelService modelService;

    @Autowired
    StoreService storeService;

    @Override
    public PromotionDto createPromotion(PromotionDto promotionDto) {
        Model model = modelService.getModelEntityById(promotionDto.getModelId());
        Store store = storeService.getStoreEntityById(promotionDto.getStoreId());

        if (promotionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        if (promotionDto.getEndDate().isBefore(promotionDto.getStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        // khuyến mãi chỉ active nếu thời gian hiện tại nằm trong khoảng startDate và endDate
        LocalDateTime now = LocalDateTime.now();
        boolean isActive =
                (promotionDto.getStartDate().isBefore(now) || promotionDto.getStartDate().isEqual(now)) &&
                        (promotionDto.getEndDate().isAfter(now) || promotionDto.getEndDate().isEqual(now));

        Promotion newPromotion = Promotion.builder()
                .promotionName(promotionDto.getPromotionName())
                .description(promotionDto.getDescription())
                .promotionType(promotionDto.getPromotionType())
                .amount(promotionDto.getAmount())
                .startDate(promotionDto.getStartDate())
                .endDate(promotionDto.getEndDate())
                .isActive(isActive)
                .model(model)
                .store(store)
                .createdAt(LocalDateTime.now())
                .build();

        promotionRepository.save(newPromotion);

        return mapToDto(newPromotion);
    }

    @Override
    @Transactional
    public List<PromotionDto> createPromotionForAllModels(PromotionDto promotionDto) {
        Store store = storeService.getStoreEntityById(promotionDto.getStoreId());

        if (promotionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        if (promotionDto.getEndDate().isBefore(promotionDto.getStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        // Lấy tất cả model
        List<Model> allModels = modelService.getAllModels().stream()
                .map(modelDto -> modelService.getModelEntityById(modelDto.getModelId()))
                .toList();

        if (allModels.isEmpty()) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }

        // Khuyến mãi chỉ active nếu thời gian hiện tại nằm trong khoảng startDate và endDate
        LocalDateTime now = LocalDateTime.now();
        boolean isActive =
                (promotionDto.getStartDate().isBefore(now) || promotionDto.getStartDate().isEqual(now)) &&
                        (promotionDto.getEndDate().isAfter(now) || promotionDto.getEndDate().isEqual(now));

        List<PromotionDto> createdPromotions = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();

        // Tạo promotion cho mỗi model
        for (Model model : allModels) {
            // Tạo promotion name unique bằng cách thêm model name
            String uniquePromotionName = promotionDto.getPromotionName() + " - " + model.getModelName();
            
            // Kiểm tra promotion name đã tồn tại chưa
            if (promotionRepository.existsByPromotionNameIgnoreCase(uniquePromotionName)) {
                logger.warn("Promotion name '{}' already exists, skipping model {}", uniquePromotionName, model.getModelName());
                continue;
            }

            Promotion newPromotion = Promotion.builder()
                    .promotionName(uniquePromotionName)
                    .description(promotionDto.getDescription())
                    .promotionType(promotionDto.getPromotionType())
                    .amount(promotionDto.getAmount())
                    .startDate(promotionDto.getStartDate())
                    .endDate(promotionDto.getEndDate())
                    .isActive(isActive)
                    .model(model)
                    .store(store)
                    .createdAt(createdAt)
                    .build();

            Promotion savedPromotion = promotionRepository.save(newPromotion);
            createdPromotions.add(mapToDto(savedPromotion));
        }

        logger.info("Created {} promotions for all models ({} models)", createdPromotions.size(), allModels.size());
        return createdPromotions;
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
    public Promotion getPromotionEntityById(int promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        return promotion;
    }

    @Override
    public List<PromotionDto> getPromotionByModelId(int modelId) {
        Model model = modelService.getModelEntityById(modelId);
        List<Promotion> promotions = promotionRepository.findByModel(model);
        return promotions.stream().map(this::mapToDto).toList();
    }

    @Override
    public PromotionDto updatePromotion(int promotionId, PromotionDto promotionDto) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }

        if (promotionDto.getModelId() != 0 && promotionDto.getModelId() != promotion.getModel().getModelId()) {
            Model model = modelService.getModelEntityById(promotionDto.getModelId());
            promotion.setModel(model);
        }

        if (promotionDto.getStoreId() != 0 && promotionDto.getStoreId() != promotion.getStore().getStoreId()) {
            Store store = storeService.getStoreEntityById(promotionDto.getStoreId());
            promotion.setStore(store);
        }

        if (promotionDto.getPromotionName() != null && !promotionDto.getPromotionName().trim().isEmpty() &&
                !promotionDto.getPromotionName().equalsIgnoreCase(promotion.getPromotionName()) &&
                !promotionRepository.existsByPromotionNameIgnoreCase(promotionDto.getPromotionName())) {
            promotion.setPromotionName(promotionDto.getPromotionName());
        }

        if (promotionDto.getPromotionType() != null) {
            promotion.setPromotionType(promotionDto.getPromotionType());
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

        LocalDateTime now = LocalDateTime.now();
        if (promotionDto.getEndDate() != null) {
            if (promotionDto.getEndDate().isBefore(promotion.getStartDate())) {
                throw new AppException(ErrorCode.INVALID_END_DATE);
            }
            promotion.setEndDate(promotionDto.getEndDate());
        }

        if (promotion.getEndDate().isBefore(now)) {
            promotion.setActive(false);
        }
        if ((promotion.getStartDate().isBefore(now) || promotion.getStartDate().isEqual(now)) &&
                (promotion.getEndDate().isAfter(now) || promotion.getEndDate().isEqual(now))) {
            promotion.setActive(true);
        }

        if (promotionDto.getDescription() != null && !promotionDto.getDescription().trim().isEmpty()) {
            promotion.setDescription(promotionDto.getDescription());
        }

        promotion.setUpdatedAt(LocalDateTime.now());

        promotionRepository.save(promotion);

        return mapToDto(promotion);
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
                .modelName(promotion.getModel().getModelName())
                .storeId(promotion.getStore().getStoreId())
                .storeName(promotion.getStore().getStoreName())
                .build();
    }

    // Runs every hour
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void updatePromotionStatus() {
        LocalDateTime now = LocalDateTime.now();

        // trả về số dòng hiệu lực bị thay đổi
        int deactivated = promotionRepository.deactivateExpiredPromotions(now);
        int activated = promotionRepository.activateCurrentPromotions(now);
        logger.info("deactivateExpiredPromotions ran at {}, deactivated {} promotions", now, deactivated);
        logger.info("deactivateExpiredPromotions ran at {}, deactivated {} promotions", now, activated);
//        List<Promotion> activePromotions = promotionRepository.findByIsActiveTrue();
//        LocalDateTime now = LocalDateTime.now();
//        for (Promotion promotion : activePromotions) {
//            if (promotion.getEndDate().isBefore(now)) {
//                promotion.setActive(false);
//                promotion.setUpdatedAt(now);
//                promotionRepository.save(promotion);
//            }
//        }
    }
}
