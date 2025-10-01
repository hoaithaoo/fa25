package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.repository.StoreRepository;
import swp391.fa25.saleElectricVehicle.service.StoreService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Override
    public StoreDto createStore(StoreDto storeDto) {
        Store storeNew = new Store();
        storeNew.setStoreName(storeDto.getStoreName());
        storeNew.setAddress(storeDto.getAddress());
        storeNew.setPhone(storeDto.getPhone());
        storeNew.setProvinceName(storeDto.getProvinceName());
        storeNew.setOwnerName(storeDto.getOwnerName());
        storeNew.setStatus(storeDto.getStatus());
        storeNew.setContractStartDate(storeDto.getContractStartDate());
        storeNew.setContractEndDate(storeDto.getContractEndDate());
        storeNew.setCreatedAt(LocalDateTime.now());
        storeRepository.save(storeNew);
        return storeDto;
    }

    @Override
    public StoreDto findStoreById(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new RuntimeException("Store with id: " + storeId + " not found");
        } else {
            StoreDto storeDto = new StoreDto();
            storeDto.setStoreId(store.getStoreId());
            storeDto.setStoreName(store.getStoreName());
            storeDto.setAddress(store.getAddress());
            storeDto.setPhone(store.getPhone());
            storeDto.setProvinceName(store.getProvinceName());
            storeDto.setOwnerName(store.getOwnerName());
            storeDto.setStatus(store.getStatus());
            storeDto.setContractStartDate(store.getContractStartDate());
            storeDto.setContractEndDate(store.getContractEndDate());
            return storeDto;
        }
    }

    @Override
    public List<StoreDto> findAllStores() {
        return storeRepository.findAll().stream().map(store -> {
            StoreDto storeDto = new StoreDto();
            storeDto.setStoreId(store.getStoreId());
            storeDto.setStoreName(store.getStoreName());
            storeDto.setAddress(store.getAddress());
            storeDto.setPhone(store.getPhone());
            storeDto.setProvinceName(store.getProvinceName());
            storeDto.setOwnerName(store.getOwnerName());
            storeDto.setStatus(store.getStatus());
            storeDto.setContractStartDate(store.getContractStartDate());
            storeDto.setContractEndDate(store.getContractEndDate());
            return storeDto;
        }).toList();
    }

    @Override
    public StoreDto updateStore(int storeId, StoreDto storeDto) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new RuntimeException("Store with id: " + storeId + " not found");
        } else {
            store.setStoreName(storeDto.getStoreName());
            store.setAddress(storeDto.getAddress());
            store.setPhone(storeDto.getPhone());
            store.setProvinceName(storeDto.getProvinceName());
            store.setOwnerName(storeDto.getOwnerName());
            store.setStatus(storeDto.getStatus());
            store.setContractStartDate(storeDto.getContractStartDate());
            store.setContractEndDate(storeDto.getContractEndDate());
            store.setUpdatedAt(LocalDateTime.now());

            storeRepository.save(store);

        }
        return StoreDto.builder()
                .storeId(store.getStoreId())
                .storeName(storeDto.getStoreName())
                .address(storeDto.getAddress())
                .phone(storeDto.getPhone())
                .provinceName(storeDto.getProvinceName())
                .ownerName(storeDto.getOwnerName())
                .status(storeDto.getStatus())
                .contractStartDate(storeDto.getContractStartDate())
                .contractEndDate(storeDto.getContractEndDate())
                .build();
    }

    @Override
    public void deleteStore(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new RuntimeException("Store with id: " + storeId + " not found");
        }
        storeRepository.delete(store);
    }


}
