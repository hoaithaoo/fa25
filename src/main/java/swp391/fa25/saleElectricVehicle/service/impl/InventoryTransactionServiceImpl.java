package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;
import swp391.fa25.saleElectricVehicle.payload.request.inventory.CreateInventoryTransactionRequest;
import swp391.fa25.saleElectricVehicle.repository.InventoryTransactionRepository;
import swp391.fa25.saleElectricVehicle.service.*;

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
    private ModelService modelService;
    @Autowired
    private ColorService colorService;
    @Autowired
    private ModelColorService modelColorService;
    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;


    private final int DISCOUNT_3_TO_10_VEHICLES = 5;
    private final int DISCOUNT_11_TO_50_VEHICLES = 8;
    private final int DISCOUNT_OVER_50_VEHICLES = 10;

    @Override
    public InventoryTransactionDto createInventoryTransaction(CreateInventoryTransactionRequest request) {
        // kiểm tra model và color tồn tại, lấy đơn giá từ model color table
        Model model= modelService.getModelEntityById(request.getModelId());
        Color color= colorService.getColorEntityById(request.getColorId());
        ModelColor modelColor= modelColorService.getModelColorEntityByModelIdAndColorId(model.getModelId(), color.getColorId());

        // lấy user và store hiện tại
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // Kiểm tra StoreStock tồn tại
        StoreStock storeStock = storeStockService.getStoreStockByStoreIdAndModelColorId(store.getStoreId(), model.getModelId());

        // Tính base amount
        BigDecimal baseAmount = modelColor.getPrice().multiply(BigDecimal.valueOf(request.getImportQuantity()));

        BigDecimal totalPrice = baseAmount;
        // Tính discount dựa trên số lượng nhập
        int discountPercentage = 0;
        if (request.getImportQuantity() > 2 && request.getImportQuantity() < 11) {
            discountPercentage = DISCOUNT_3_TO_10_VEHICLES;
            BigDecimal discountAmount = baseAmount.multiply(BigDecimal.valueOf(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 5%
            totalPrice = baseAmount.subtract(discountAmount);
        } else if (request.getImportQuantity() > 10 && request.getImportQuantity() < 51) {
            discountPercentage = DISCOUNT_11_TO_50_VEHICLES;
            BigDecimal discountAmount = baseAmount.multiply(BigDecimal.valueOf(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 8%
            totalPrice = baseAmount.subtract(discountAmount);
        } else if (request.getImportQuantity() > 50) {
            discountPercentage = DISCOUNT_OVER_50_VEHICLES;
            BigDecimal discountAmount = baseAmount.multiply(BigDecimal.valueOf(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 10%
            totalPrice = baseAmount.subtract(discountAmount);
        }

        
        // Đảm bảo totalPrice không bao giờ âm (nếu âm thì set = 0)
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            totalPrice = BigDecimal.ZERO;
        }

        // Tính dept = totalPrice - deposit
//        BigDecimal dept = totalPrice.subtract(BigDecimal.valueOf(dto.getDeposit()));

        // Tạo InventoryTransaction
        InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                .unitBasePrice(modelColor.getPrice())
                .importQuantity(request.getImportQuantity())
                .discountPercentage(discountPercentage)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .storeStock(storeStock)
                .status(InventoryTransactionStatus.PENDING)
                .build();

        InventoryTransaction saved = inventoryTransactionRepository.save(inventoryTransaction);

        return mapToDto(saved);
    }

    @Override
    public InventoryTransactionDto getInventoryTransactionById(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }
        
        return mapToDto(transaction);
    }

    @Override
    public InventoryTransaction getInventoryTransactionEntityById(int inventoryId) {
        return inventoryTransactionRepository.findById(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND));
    }

    @Override
    public List<InventoryTransactionDto> getAllInventoryTransactions() {
        // Lấy store của user hiện tại
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        // Chỉ lấy transactions của store hiện tại
        return inventoryTransactionRepository.findByStoreStock_Store_StoreId(currentStore.getStoreId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDto> getInventoryTransactionsByStoreStock(int storeStockId) {
        // Kiểm tra StoreStock tồn tại
        StoreStock storeStock = storeStockService.getStoreStockEntityById(storeStockId);
        
        // Kiểm tra StoreStock có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (storeStock.getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }

        return inventoryTransactionRepository.findByStoreStock_StockId(storeStockId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDto> getInventoryTransactionsByDateRange(
            LocalDateTime start, LocalDateTime end) {
        
        // Lấy store của user hiện tại
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        // Chỉ lấy transactions của store hiện tại trong khoảng ngày
        return inventoryTransactionRepository.findByStoreStock_Store_StoreIdAndOrderDateBetween(
                currentStore.getStoreId(), start, end).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

//    @Override
//    @Transactional
//    public InventoryTransactionDto updateInventoryTransaction(
//            int inventoryId, CreateInventoryTransactionRequest dto) {
//
//        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
//
//        // Chỉ cho phép update khi status là PENDING
//        if (transaction.getStatus() != InventoryTransactionStatus.PENDING) {
//            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_UPDATE);
//        }
//
//        // Cập nhật các field nếu có trong DTO (khác null)
//        if (dto.getUnitBasePrice() != null) {
//            transaction.setUnitBasePrice(dto.getUnitBasePrice());
//        }
//
//        if (dto.getImportQuantity() != 0) {
//            transaction.setImportQuantity(dto.getImportQuantity());
//        }
//
//        if (dto.getDiscountPercentage() != 0) {
//            transaction.setDiscountPercentage(dto.getDiscountPercentage());
//        }
//
//        if (dto.getDeliveryDate() != null) {
//            transaction.setDeliveryDate(dto.getDeliveryDate());
//        }
//
//        // Không cho phép đổi StoreStock sau khi đã tạo transaction
//        // if (dto.getStoreStockId() != 0) {
//        //     StoreStock newStoreStock = storeStockService.getStoreStockEntityById(dto.getStoreStockId());
//        //     transaction.setStoreStock(newStoreStock);
//        // }
//
//        // Tính lại totalPrice
//        BigDecimal baseAmount = transaction.getUnitBasePrice()
//                .multiply(BigDecimal.valueOf(transaction.getImportQuantity()));
//
//        BigDecimal discountAmount = baseAmount
//                .multiply(BigDecimal.valueOf(transaction.getDiscountPercentage()))
//                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//
//        BigDecimal totalPrice = baseAmount.subtract(discountAmount);
//
//        // Đảm bảo totalPrice không bao giờ âm (nếu âm thì set = 0)
//        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
//            totalPrice = BigDecimal.ZERO;
//        }
//
//        transaction.setTotalPrice(totalPrice);
//
//        InventoryTransaction updated = inventoryTransactionRepository.save(transaction);
//        return mapToDto(updated);
//    }

    @Override
    @Transactional
    public void deleteInventoryTransaction(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

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
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

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
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

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
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

        // Chỉ cho phép start shipping khi status là PAYMENT_CONFIRMED
        if (transaction.getStatus() != InventoryTransactionStatus.PAYMENT_CONFIRMED) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_START_SHIPPING);
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
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

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

    @Override
    @Transactional
    public InventoryTransactionDto uploadReceipt(int inventoryId, String imageUrl) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

        // Chỉ cho phép upload receipt khi status là CONFIRMED
        if (transaction.getStatus() != InventoryTransactionStatus.CONFIRMED) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_UPLOAD_RECEIPT);
        }

        // Cập nhật imageUrl và status thành FILE_UPLOADED
        transaction.setImageUrl(imageUrl);
        transaction.setStatus(InventoryTransactionStatus.FILE_UPLOADED);

        InventoryTransaction saved = inventoryTransactionRepository.save(transaction);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public InventoryTransactionDto confirmPayment(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

        // Chỉ cho phép confirm payment khi status là FILE_UPLOADED
        if (transaction.getStatus() != InventoryTransactionStatus.FILE_UPLOADED) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_CONFIRM_PAYMENT);
        }

        // Cập nhật status thành PAYMENT_CONFIRMED
        transaction.setStatus(InventoryTransactionStatus.PAYMENT_CONFIRMED);

        InventoryTransaction saved = inventoryTransactionRepository.save(transaction);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public InventoryTransactionDto cancelRequest(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
        
        // Kiểm tra transaction có thuộc store của user hiện tại không
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        if (transaction.getStoreStock().getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_NOT_FOUND);
        }

        // Chỉ cho phép cancel khi status là PENDING
        if (transaction.getStatus() != InventoryTransactionStatus.PENDING) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_CANCEL);
        }

        // Cập nhật status thành CANCELLED
        transaction.setStatus(InventoryTransactionStatus.CANCELLED);

        InventoryTransaction saved = inventoryTransactionRepository.save(transaction);
        return mapToDto(saved);
    }

    // Helper method: Map Entity sang DTO
    private InventoryTransactionDto mapToDto(InventoryTransaction transaction) {
        BigDecimal totalBasePrice = transaction.getUnitBasePrice()
                .multiply(BigDecimal.valueOf(transaction.getImportQuantity()));
        return InventoryTransactionDto.builder()
                .inventoryId(transaction.getInventoryId())
                .unitBasePrice(transaction.getUnitBasePrice())
                .importQuantity(transaction.getImportQuantity())
                .totalBasePrice(totalBasePrice)
                .discountPercentage(transaction.getDiscountPercentage())
                .discountAmount(totalBasePrice.multiply(BigDecimal.valueOf(transaction.getDiscountPercentage())))
                .totalPrice(transaction.getTotalPrice())
//                .deposit(0) // Field không còn trong entity, set default
//                .dept(null) // Field không còn trong entity, set null
                .orderDate(transaction.getOrderDate()) // Sử dụng orderDate thay vì transactionDate
                .deliveryDate(transaction.getDeliveryDate())
                .imageUrl(transaction.getImageUrl())
                .status(transaction.getStatus())
                .modelId(transaction.getStoreStock().getModelColor().getModel().getModelId())
                .modelName(transaction.getStoreStock().getModelColor().getModel().getModelName())
                .colorId(transaction.getStoreStock().getModelColor().getColor().getColorId())
                .colorName(transaction.getStoreStock().getModelColor().getColor().getColorName())
//                .storeStockId(transaction.getStoreStock().getStockId())
                .build();
    }
}
