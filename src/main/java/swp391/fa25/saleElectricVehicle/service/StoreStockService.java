package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.StoreStockDto;

import java.math.BigDecimal;

public interface StoreStockService {
    StoreStockDto createStoreStock(StoreStockDto createStoreStock);
    StoreStockDto updatePriceOfStore(int stockId, BigDecimal price);
    StoreStockDto updateQuantity(int stockId, int quantity);
}
