package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;

import java.math.BigDecimal;
import java.util.List;

public interface StoreStockService {
    StoreStockDto createStoreStock(StoreStockDto createStoreStock);
    List<StoreStockDto> getAllStoreStock();
//    StoreStockDto getStoreStockByStoreIdAndModelIdAndColorId(int storeId, int modelId, int colorId);
    StoreStockDto updatePriceOfStore(int stockId, BigDecimal price);
    StoreStockDto updateQuantity(int stockId, int quantity);
    void deleteStoreStock(int stockId);
}
