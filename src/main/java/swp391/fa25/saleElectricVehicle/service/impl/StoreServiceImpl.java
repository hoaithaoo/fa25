package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
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
        if (storeRepository.existsByStoreName(storeDto.getStoreName())) {
            throw new AppException(ErrorCode.STORE_EXISTED);
        }

        if (storeDto.getContractEndDate().isBefore(storeDto.getContractStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        Store store = Store.builder()
                .storeName(storeDto.getStoreName())
                .address(storeDto.getAddress())
                .phone(storeDto.getPhone())
                .provinceName(storeDto.getProvinceName())
                .ownerName(storeDto.getOwnerName())
                .status(storeDto.getStatus())
                .contractStartDate(storeDto.getContractStartDate())
                .contractEndDate(storeDto.getContractEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        storeRepository.save(store);

        return storeDto;
    }

    @Override
    public List<StoreDto> findStoreByName(String name) {
        List<Store> store = storeRepository.findStoresByStoreNameContaining(name);
        if (store.isEmpty()) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        return store.stream().map(s -> {
            StoreDto storeDto = new StoreDto();
            storeDto.setStoreId(s.getStoreId());
            storeDto.setStoreName(s.getStoreName());
            storeDto.setAddress(s.getAddress());
            storeDto.setPhone(s.getPhone());
            storeDto.setProvinceName(s.getProvinceName());
            storeDto.setOwnerName(s.getOwnerName());
            storeDto.setStatus(s.getStatus());
            storeDto.setContractStartDate(s.getContractStartDate());
            storeDto.setContractEndDate(s.getContractEndDate());
            return storeDto;
        }).toList();
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
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        if (storeDto.getContractEndDate().isBefore(storeDto.getContractStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        if (storeDto.getStoreName() != null && !storeDto.getStoreName().trim().isEmpty()) {
            store.setStoreName(storeDto.getStoreName());
        }

        if (storeDto.getAddress() != null && !storeDto.getAddress().trim().isEmpty()) {
            store.setAddress(storeDto.getAddress());
        }

        if (storeDto.getPhone() != null && !storeDto.getPhone().trim().isEmpty()) {
            if (storeRepository.existsByPhone(storeDto.getPhone())) {
                throw new AppException(ErrorCode.PHONE_EXISTED);
            }
            store.setPhone(storeDto.getPhone());
        }

        if (storeDto.getProvinceName() != null && !storeDto.getProvinceName().trim().isEmpty()) {
            store.setProvinceName(storeDto.getProvinceName());
        }

        if (storeDto.getOwnerName() != null && !storeDto.getOwnerName().trim().isEmpty()) {
            store.setOwnerName(storeDto.getOwnerName());
        }

        if (storeDto.getStatus() != null) {
            store.setStatus(storeDto.getStatus());
        }

        store.setUpdatedAt(LocalDateTime.now());

        storeRepository.save(store);

        return storeDto;
    }

    @Override
    public void deleteStore(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        storeRepository.delete(store);
    }
}
