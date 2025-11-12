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
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.PromotionDto;
import swp391.fa25.saleElectricVehicle.repository.PromotionRepository;
import swp391.fa25.saleElectricVehicle.service.ModelService;
import swp391.fa25.saleElectricVehicle.service.PromotionService;
import swp391.fa25.saleElectricVehicle.service.StoreService;
import swp391.fa25.saleElectricVehicle.service.UserService;

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

    @Autowired
    UserService userService;

    @Override
    public PromotionDto createPromotion(PromotionDto promotionDto) {
        // Kiểm tra quyền: chỉ manager của store mới được tạo promotion
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        // Kiểm tra user có phải là manager không
        // if (!currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng")) {
        //     throw new AppException(ErrorCode.UNAUTHORIZED_CREATE_PROMOTION);
        // }
        
        // Kiểm tra user có thuộc store này không
        // if (currentUser.getStore() == null || currentUser.getStore().getStoreId() != store.getStoreId()) {
        //     throw new AppException(ErrorCode.UNAUTHORIZED_CREATE_PROMOTION);
        // }
        
        Model model = modelService.getModelEntityById(promotionDto.getModelId());

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
                .isManufacturerPromotion(false) // Promotion của đại lý
                .createdAt(LocalDateTime.now())
                .build();

        promotionRepository.save(newPromotion);

        return mapToDto(newPromotion);
    }

    @Override
    @Transactional
    public List<PromotionDto> createPromotionForAllModels(PromotionDto promotionDto) {
        // Kiểm tra quyền: chỉ manager của store mới được tạo promotion
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        // Kiểm tra user có phải là manager không
//        if (!currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng")) {
//            throw new AppException(ErrorCode.UNAUTHORIZED_CREATE_PROMOTION);
//        }
        
        // Kiểm tra user có thuộc store này không
//        if (currentUser.getStore() == null || currentUser.getStore().getStoreId() != store.getStoreId()) {
//            throw new AppException(ErrorCode.UNAUTHORIZED_CREATE_PROMOTION);
//        }

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
            // String uniquePromotionName = promotionDto.getPromotionName() + " - " + model.getModelName();
            
            // // Kiểm tra promotion name đã tồn tại chưa
            // if (promotionRepository.existsByPromotionNameIgnoreCase(uniquePromotionName)) {
            //     logger.warn("Promotion name '{}' already exists, skipping model {}", uniquePromotionName, model.getModelName());
            //     continue;
            // }

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
                    .createdAt(createdAt)
                    .build();

            Promotion savedPromotion = promotionRepository.save(newPromotion);
            createdPromotions.add(mapToDto(savedPromotion));
        }

        // logger.info("Created {} promotions for all models ({} models)", createdPromotions.size(), allModels.size());
        return createdPromotions;
    }

    @Override
    public List<PromotionDto> getPromotionByName(String promotionName) {
        // Lấy user hiện tại và store của user
        User currentUser = userService.getCurrentUserEntity();
        
        // Kiểm tra user có store không
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        
        int storeId = currentUser.getStore().getStoreId();
        List<Promotion> promotion = promotionRepository.findByStore_StoreIdAndPromotionNameContainingIgnoreCase(storeId, promotionName);
        if (promotion.isEmpty()) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }

        return promotion.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<PromotionDto> getAllPromotions() {
        // Lấy user hiện tại và store của user
        User currentUser = userService.getCurrentUserEntity();
        
        // Kiểm tra user có store không
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        
        int storeId = currentUser.getStore().getStoreId();
        List<Promotion> promotions = promotionRepository.findByStore_StoreId(storeId);
        return promotions.stream().map(this::mapToDto).toList();
    }

    @Override
    public Promotion getPromotionEntityById(int promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        
        // Nếu là promotion của hãng, cho phép lấy
        if (promotion.isManufacturerPromotion()) {
            return promotion;
        }
        
        // Nếu là promotion của đại lý, kiểm tra promotion có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        if (currentUser.getStore() == null || 
            promotion.getStore() == null ||
            promotion.getStore().getStoreId() != currentUser.getStore().getStoreId()) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        
        return promotion;
    }

    @Override
    public List<PromotionDto> getPromotionByModelId(int modelId) {
        // Lấy user hiện tại và store của user
        User currentUser = userService.getCurrentUserEntity();
        
        // Kiểm tra user có store không
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        
        int storeId = currentUser.getStore().getStoreId();
        Model model = modelService.getModelEntityById(modelId);
        // Chỉ lấy promotion của đại lý (không phải hãng) và còn active
        List<Promotion> promotions = promotionRepository.findByStore_StoreIdAndModel(storeId, model);
        return promotions.stream()
                .filter(p -> !p.isManufacturerPromotion() && p.isActive()) // Chỉ lấy promotion của đại lý và active
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public PromotionDto updatePromotion(int promotionId, PromotionDto promotionDto) {
        // Lấy user hiện tại và kiểm tra quyền
        User currentUser = userService.getCurrentUserEntity();
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        
        // Kiểm tra promotion có thuộc store của user hiện tại không
        if (promotion.getStore().getStoreId() != currentUser.getStore().getStoreId()) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }

        if (promotionDto.getModelId() != 0 && promotionDto.getModelId() != promotion.getModel().getModelId()) {
            Model model = modelService.getModelEntityById(promotionDto.getModelId());
            promotion.setModel(model);
        }

        // Không cho phép thay đổi store của promotion
        // if (promotionDto.getStoreId() != 0 && promotionDto.getStoreId() != promotion.getStore().getStoreId()) {
        //     Store store = storeService.getStoreEntityById(promotionDto.getStoreId());
        //     promotion.setStore(store);
        // }

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
        // Lấy user hiện tại và kiểm tra quyền
        User currentUser = userService.getCurrentUserEntity();
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        
        // Kiểm tra promotion có thuộc store của user hiện tại không
        if (promotion.getStore().getStoreId() != currentUser.getStore().getStoreId()) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        
        promotionRepository.delete(promotion);
    }

    @Override
    public PromotionDto createManufacturerPromotion(PromotionDto promotionDto) {
        // Kiểm tra quyền: chỉ hãng (Nhân viên hãng xe hoặc Admin) mới được tạo promotion
        // User currentUser = userService.getCurrentUserEntity();
        // String roleName = currentUser.getRole().getRoleName();
        
        // if (!roleName.equalsIgnoreCase("Nhân viên hãng xe") && 
        //     !roleName.equalsIgnoreCase("Quản trị viên")) {
        //     throw new AppException(ErrorCode.UNAUTHORIZED_CREATE_PROMOTION);
        // }
        
        Model model = modelService.getModelEntityById(promotionDto.getModelId());

        if (promotionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        if (promotionDto.getEndDate().isBefore(promotionDto.getStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        // Khuyến mãi chỉ active nếu thời gian hiện tại nằm trong khoảng startDate và endDate
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
                .store(null) // Promotion của hãng không có store
                .isManufacturerPromotion(true) // Đánh dấu là promotion của hãng
                .createdAt(LocalDateTime.now())
                .build();

        Promotion saved = promotionRepository.save(newPromotion);
        return mapToDto(saved);
    }

    @Override
    public List<PromotionDto> getManufacturerPromotionsByModelId(int modelId) {
        Model model = modelService.getModelEntityById(modelId);
        List<Promotion> promotions = promotionRepository.findByIsManufacturerPromotionTrueAndModelAndIsActiveTrue(model);
        return promotions.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<PromotionDto> getAllManufacturerPromotions() {
        List<Promotion> promotions = promotionRepository.findByIsManufacturerPromotionTrueAndIsActiveTrue();
        return promotions.stream().map(this::mapToDto).toList();
    }

    @Override
    public Promotion getStorePromotionEntityById(int promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        
        // Không cho phép lấy promotion của hãng
        if (promotion.isManufacturerPromotion()) {
            throw new AppException(ErrorCode.MANUFACTURER_PROMOTION_NOT_ALLOWED_FOR_ORDER);
        }
        
        // Kiểm tra promotion có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        
        if (promotion.getStore() == null ||
            promotion.getStore().getStoreId() != currentUser.getStore().getStoreId()) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXIST);
        }
        
        // Kiểm tra promotion có active không
        if (!promotion.isActive()) {
            throw new AppException(ErrorCode.PROMOTION_EXPIRED);
        }
        
        return promotion;
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
                .storeId(promotion.getStore() != null ? promotion.getStore().getStoreId() : null)
                .storeName(promotion.getStore() != null ? promotion.getStore().getStoreName() : null)
                .isManufacturerPromotion(promotion.isManufacturerPromotion())
                .build();
    }

    // Runs every hour at minute 0 (e.g., 1:00, 2:00, 3:00, ...)
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void updatePromotionStatus() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Checking promotion status at {}", now);

        // Deactivate expired promotions (endDate < now)
        int deactivated = promotionRepository.deactivateExpiredPromotions(now);
        logger.info("Deactivated {} expired promotions", deactivated);

        // Activate current promotions (startDate <= now <= endDate)
        int activated = promotionRepository.activateCurrentPromotions(now);
        logger.info("Activated {} current promotions", activated);
    }
}
