package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;

import java.util.List;

public interface StoreService {
    StoreDto createStore(StoreDto storeDto);
    StoreDto findStoreById(int storeId);
    List<StoreDto> findAllStores();
}
