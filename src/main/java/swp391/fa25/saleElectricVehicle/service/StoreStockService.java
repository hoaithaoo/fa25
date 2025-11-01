package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.StoreStock;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;

import java.math.BigDecimal;
import java.util.List;

public interface StoreStockService {
    StoreStockDto createStoreStock(StoreStockDto createStoreStock);
    List<StoreStockDto> getAllStoreStock();
    StoreStock getStoreStockEntityById(int stockId);
    StoreStock getStoreStockByStoreIdAndModelColorId(int storeId, int modelColorId);
    StoreStock getStoreStockByStoreIdAndModelColorIdWithLock(int storeId, int modelColorId);
    int getQuantityByModelIdAndColorId(int modelId, int colorId);
    StoreStockDto updatePriceOfStore(int stockId, BigDecimal price);
    StoreStockDto updateQuantity(int stockId, int quantity);
    void deleteStoreStock(int stockId);
}
