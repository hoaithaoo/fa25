package swp391.fa25.saleElectricVehicle.service;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import swp391.fa25.saleElectricVehicle.entity.StoreStock;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;

import java.math.BigDecimal;
import java.util.List;

public interface StoreStockService {
    StoreStockDto createStoreStock(StoreStockDto createStoreStock);
    List<StoreStockDto> getAllStoreStock();
    StoreStock getStoreStockEntityById(int stockId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    StoreStock getStoreStockByStoreIdAndModelColorId(int storeId, int modelColorId);
    StoreStockDto updatePriceOfStore(int stockId, BigDecimal price);
    StoreStockDto updateQuantity(int stockId, int quantity);
    void deleteStoreStock(int stockId);
}
