package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;

import java.util.List;

public interface StoreService {
    StoreDto createStore(StoreDto storeDto);
    Store getStoreEntityById(int storeId);
    List<StoreDto> getStoreByNameContaining(String storeName);
//    StoreDto getStoreByName(String storeName);
    Store getStoreEntityByName(String storeName);
    Store getCurrentStoreEntity(int userId);
    List<StoreDto> getAllStores();
    StoreDto updateStore(int storeId, StoreDto storeDto);
    void deleteStore(int storeId);
}
