package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;
import java.util.List;

public interface StoreStockService {
    // Basic CRUD operations only
    StoreStockDto createStoreStock(StoreStockDto storeStockDto);
    StoreStockDto getStoreStockById(int stockId);
    List<StoreStockDto> getAllStoreStocks();
    StoreStockDto updateStoreStock(int stockId, StoreStockDto storeStockDto);
    void deleteStoreStock(int stockId);
}