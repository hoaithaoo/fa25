package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;

import java.util.List;

public interface StoreService {
    StoreDto createStore(StoreDto storeDto);
    Store getStoreEntityById(int storeId);
    List<StoreDto> getStoreByNameContaining(String storeName);
    Store getCurrentStoreEntity(int userId);
    List<StoreDto> getAllStores();
    List<StoreDto> getAllActiveStores();
    StoreDto updateStore(int storeId, StoreDto storeDto);
    void deleteStore(int storeId);
}
