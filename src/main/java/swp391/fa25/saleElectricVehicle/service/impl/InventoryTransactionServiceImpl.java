package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.entity.StoreStock;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;
import swp391.fa25.saleElectricVehicle.repository.InventoryTransactionRepository;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionService;
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

    @Override
    public InventoryTransactionDto createInventoryTransaction(InventoryTransactionDto dto) { // ✅ Đổi tham số
        // Kiểm tra StoreStock tồn tại
        StoreStock storeStock = storeStockService.getStoreStockEntityById(dto.getStoreStockId());

        // Tính totalPrice = unitBasePrice * importQuantity * (1 - discountPercentage/100)
        BigDecimal baseAmount = dto.getUnitBasePrice()
                .multiply(BigDecimal.valueOf(dto.getImportQuantity()));

        BigDecimal discountAmount = baseAmount
                .multiply(BigDecimal.valueOf(dto.getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalPrice = baseAmount.subtract(discountAmount);

        // Tính dept = totalPrice - deposit
        BigDecimal dept = totalPrice.subtract(BigDecimal.valueOf(dto.getDeposit()));

        // Tạo InventoryTransaction
        InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                .unitBasePrice(dto.getUnitBasePrice())
                .importQuantity(dto.getImportQuantity())
                .discountPercentage(dto.getDiscountPercentage())
                .totalPrice(totalPrice)
                .deposit(dto.getDeposit())
                .dept(dept)
                .transactionDate(LocalDateTime.now())
                .deliveryDate(dto.getDeliveryDate())
                .storeStock(storeStock)
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

        return inventoryTransactionRepository.findByStoreStock_StoreStockId(storeStockId).stream()
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
    public InventoryTransactionDto updateInventoryTransaction(
            int inventoryId, InventoryTransactionDto dto) { // ✅ Đổi tham số

        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);

        // Cập nhật các field nếu có trong DTO (khác null)
        if (dto.getUnitBasePrice() != null) {
            transaction.setUnitBasePrice(dto.getUnitBasePrice());
        }

        if (dto.getImportQuantity() != 0) { // ✅ Check != 0 thay vì != null
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

        if (dto.getStoreStockId() != 0) {
            StoreStock newStoreStock = storeStockService.getStoreStockEntityById(dto.getStoreStockId());
            transaction.setStoreStock(newStoreStock);
        }

        // Tính lại totalPrice và dept
        BigDecimal baseAmount = transaction.getUnitBasePrice()
                .multiply(BigDecimal.valueOf(transaction.getImportQuantity()));

        BigDecimal discountAmount = baseAmount
                .multiply(BigDecimal.valueOf(transaction.getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalPrice = baseAmount.subtract(discountAmount);
        transaction.setTotalPrice(totalPrice);

        BigDecimal dept = totalPrice.subtract(BigDecimal.valueOf(transaction.getDeposit()));
        transaction.setDept(dept);

        InventoryTransaction updated = inventoryTransactionRepository.save(transaction);
        return mapToDto(updated);
    }

    @Override
    public void deleteInventoryTransaction(int inventoryId) {
        InventoryTransaction transaction = getInventoryTransactionEntityById(inventoryId);
        inventoryTransactionRepository.delete(transaction);
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
                .storeStockId(transaction.getStoreStock().getStoreStockId())
                .build();
    }
}
