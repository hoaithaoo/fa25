package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.payload.response.store.StoreMonthlyRevenueResponse;

import java.util.List;

public interface StoreService {
    StoreDto createStore(StoreDto storeDto);
    StoreDto addStoreImagePath(int storeId, String imagePath);
    StoreDto getStoreById(int storeId);
    Store getStoreEntityById(int storeId);
    List<StoreDto> getStoreByNameContaining(String storeName);
    Store getCurrentStoreEntity(int userId);
    List<StoreDto> getAllStores();
    List<StoreDto> getAllActiveStores();
    StoreDto updateStore(int storeId, StoreDto storeDto);
    void deleteStore(int storeId);
    List<StoreMonthlyRevenueResponse> getMonthlyRevenueForAllStores();
    StoreDto toggleStoreStatus(int storeId);
}
