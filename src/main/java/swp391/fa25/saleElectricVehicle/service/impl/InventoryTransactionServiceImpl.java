package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.entity.Promotion;
import swp391.fa25.saleElectricVehicle.entity.StoreStock;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PromotionType;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;
import swp391.fa25.saleElectricVehicle.repository.InventoryTransactionRepository;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionService;
import swp391.fa25.saleElectricVehicle.service.PromotionService;
import swp391.fa25.saleElectricVehicle.service.StoreStockService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private StoreStockService storeStockService;

    @Autowired
    private PromotionService promotionService;

    @Override
    public InventoryTransactionDto createInventoryTransaction(InventoryTransactionDto dto) {
        // Kiểm tra StoreStock tồn tại
        StoreStock storeStock = storeStockService.getStoreStockEntityById(dto.getStoreStockId());

        // Tính base amount
        BigDecimal baseAmount = dto.getUnitBasePrice()
                .multiply(BigDecimal.valueOf(dto.getImportQuantity()));

        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion promotion = null;
        int discountPercentage = dto.getDiscountPercentage();

        // Nếu có promotionId, áp dụng promotion của hãng
        if (dto.getPromotionId() != null && dto.getPromotionId() > 0) {
            promotion = promotionService.getPromotionEntityById(dto.getPromotionId());
            
            // Kiểm tra promotion có phải của hãng không
            if (!promotion.isManufacturerPromotion()) {
                throw new AppException(ErrorCode.PROMOTION_NOT_EXIST, 
                        "Chỉ có thể áp dụng promotion của hãng cho inventory transaction");
            }
            
            // Kiểm tra promotion có active không
            if (!promotion.isActive()) {
                throw new AppException(ErrorCode.PROMOTION_EXPIRED, 
                        "Promotion không còn hiệu lực");
            }
            
            // Tính discount dựa trên promotion
            if (promotion.getPromotionType() == PromotionType.PERCENTAGE) {
                discountAmount = baseAmount
                        .multiply(promotion.getAmount())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                discountPercentage = promotion.getAmount().intValue();
            } else if (promotion.getPromotionType() == PromotionType.FIXED_AMOUNT) {
                discountAmount = promotion.getAmount().multiply(BigDecimal.valueOf(dto.getImportQuantity()));
                // Tính discountPercentage từ fixed amount
                if (baseAmount.compareTo(BigDecimal.ZERO) > 0) {
                    discountPercentage = discountAmount
                            .multiply(BigDecimal.valueOf(100))
                            .divide(baseAmount, 2, RoundingMode.HALF_UP)
                            .intValue();
                }
            }
        } else {
            // Nếu không có promotion, dùng discountPercentage từ DTO
            discountAmount = baseAmount
                    .multiply(BigDecimal.valueOf(dto.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal totalPrice = baseAmount.subtract(discountAmount);
        
        // Đảm bảo totalPrice không bao giờ âm (nếu âm thì set = 0)
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            totalPrice = BigDecimal.ZERO;
        }

        // Tính dept = totalPrice - deposit
        BigDecimal dept = totalPrice.subtract(BigDecimal.valueOf(dto.getDeposit()));

        // Tạo InventoryTransaction
        InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                .unitBasePrice(dto.getUnitBasePrice())
                .importQuantity(dto.getImportQuantity())
                .discountPercentage(discountPercentage)
                .totalPrice(totalPrice)
                .deposit(dto.getDeposit())
                .dept(dept)
                .transactionDate(LocalDateTime.now())
                .deliveryDate(dto.getDeliveryDate())
                .storeStock(storeStock)
                .promotion(promotion)
                .build();

        InventoryTransaction saved = inventoryTransactionRepository.save(inventoryTransaction);

        return mapToDto(saved);
    }

    @Override
    public InventoryTransactionDto getInventoryTransactionById(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
        return mapToDto(transaction);
    }

    @Override
    public InventoryTransaction getInventoryTransactionEntityById(int inventoryId) {
        return inventoryTransactionRepository.findById(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND));
    }

    @Override
    public List<InventoryTransactionDto> getAllInventoryTransactions() {
        return inventoryTransactionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDto> getInventoryTransactionsByStoreStock(int storeStockId) {
        // Kiểm tra StoreStock tồn tại
        storeStockService.getStoreStockEntityById(storeStockId);

        return inventoryTransactionRepository.findByStoreStock_StockId(storeStockId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDto> getInventoryTransactionsByDateRange(
            LocalDateTime start, LocalDateTime end) {

        return inventoryTransactionRepository.findByTransactionDateBetween(start, end).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryTransactionDto updateInventoryTransaction(
            int inventoryId, InventoryTransactionDto dto) {

        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);

        // Chỉ cho phép update khi status là PENDING
        if (transaction.getStatus() != InventoryTransactionStatus.PENDING) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_UPDATE);
        }

        // Cập nhật các field nếu có trong DTO (khác null)
        if (dto.getUnitBasePrice() != null) {
            transaction.setUnitBasePrice(dto.getUnitBasePrice());
        }

        if (dto.getImportQuantity() != 0) {
            transaction.setImportQuantity(dto.getImportQuantity());
        }

        if (dto.getDiscountPercentage() != 0) {
            transaction.setDiscountPercentage(dto.getDiscountPercentage());
        }

        if (dto.getDeposit() != 0) {
            transaction.setDeposit(dto.getDeposit());
        }

        if (dto.getDeliveryDate() != null) {
            transaction.setDeliveryDate(dto.getDeliveryDate());
        }

        // Không cho phép đổi StoreStock sau khi đã tạo transaction
        // if (dto.getStoreStockId() != 0) {
        //     StoreStock newStoreStock = storeStockService.getStoreStockEntityById(dto.getStoreStockId());
        //     transaction.setStoreStock(newStoreStock);
        // }

        // Tính lại totalPrice và dept
        BigDecimal baseAmount = transaction.getUnitBasePrice()
                .multiply(BigDecimal.valueOf(transaction.getImportQuantity()));

        BigDecimal discountAmount = baseAmount
                .multiply(BigDecimal.valueOf(transaction.getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalPrice = baseAmount.subtract(discountAmount);
        
        // Đảm bảo totalPrice không bao giờ âm (nếu âm thì set = 0)
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            totalPrice = BigDecimal.ZERO;
        }
        
        transaction.setTotalPrice(totalPrice);

        BigDecimal dept = totalPrice.subtract(BigDecimal.valueOf(transaction.getDeposit()));
        transaction.setDept(dept);

        InventoryTransaction updated = inventoryTransactionRepository.save(transaction);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteInventoryTransaction(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);

        // Chỉ cho phép xóa khi status là PENDING
        if (transaction.getStatus() != InventoryTransactionStatus.PENDING) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_DELETE);
        }

        inventoryTransactionRepository.delete(transaction);
    }

    @Override
    @Transactional
    public InventoryTransactionDto acceptRequest(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);

        // Chỉ cho phép accept khi status là PENDING
        if (transaction.getStatus() != InventoryTransactionStatus.PENDING) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_CONFIRM);
        }

        // Cập nhật status thành CONFIRMED
        transaction.setStatus(InventoryTransactionStatus.CONFIRMED);

        InventoryTransaction saved = inventoryTransactionRepository.save(transaction);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public InventoryTransactionDto rejectRequest(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);

        // Chỉ cho phép reject khi status là PENDING
        if (transaction.getStatus() != InventoryTransactionStatus.PENDING) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_REJECT);
        }

        // Cập nhật status thành REJECTED
        transaction.setStatus(InventoryTransactionStatus.REJECTED);

        InventoryTransaction saved = inventoryTransactionRepository.save(transaction);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public InventoryTransactionDto startShipping(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);

        // Chỉ cho phép start shipping khi status là CONFIRMED
        if (transaction.getStatus() != InventoryTransactionStatus.CONFIRMED) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_DELIVER);
        }

        // Cập nhật status thành IN_TRANSIT
        transaction.setStatus(InventoryTransactionStatus.IN_TRANSIT);

        InventoryTransaction saved = inventoryTransactionRepository.save(transaction);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public InventoryTransactionDto confirmDelivery(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);

        // Chỉ cho phép confirm delivery khi status là IN_TRANSIT
        if (transaction.getStatus() != InventoryTransactionStatus.IN_TRANSIT) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_DELIVERED);
        }

        // Cập nhật status thành DELIVERED
        transaction.setStatus(InventoryTransactionStatus.DELIVERED);

        // Cập nhật tồn kho: tăng quantity bằng importQuantity
        StoreStock storeStock = transaction.getStoreStock();
        storeStock.setQuantity(storeStock.getQuantity() + transaction.getImportQuantity());
        storeStockService.updateStoreStock(storeStock);

        // Lưu transaction
        InventoryTransaction saved = inventoryTransactionRepository.save(transaction);

        return mapToDto(saved);
    }

    // Helper method: Map Entity sang DTO
    private InventoryTransactionDto mapToDto(InventoryTransaction transaction) {
        return InventoryTransactionDto.builder()
                .inventoryId(transaction.getInventoryId())
                .unitBasePrice(transaction.getUnitBasePrice())
                .importQuantity(transaction.getImportQuantity())
                .discountPercentage(transaction.getDiscountPercentage())
                .totalPrice(transaction.getTotalPrice())
                .deposit(transaction.getDeposit())
                .dept(transaction.getDept())
                .transactionDate(transaction.getTransactionDate())
                .deliveryDate(transaction.getDeliveryDate())
                .status(transaction.getStatus())
                .storeStockId(transaction.getStoreStock().getStockId())
                .promotionId(transaction.getPromotion() != null ? transaction.getPromotion().getPromotionId() : null)
                .build();
    }
}
