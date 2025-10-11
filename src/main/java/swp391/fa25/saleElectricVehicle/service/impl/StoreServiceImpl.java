package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.StoreStatus;
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

        if (storeDto.getContractEndDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_END_DATE_TIME);
        }

        Store store = Store.builder()
                .storeName(storeDto.getStoreName())
                .address(storeDto.getAddress())
                .phone(storeDto.getPhone())
                .provinceName(storeDto.getProvinceName())
                .ownerName(storeDto.getOwnerName())
                .status(StoreStatus.ACTIVE)
                .contractStartDate(storeDto.getContractStartDate())
                .contractEndDate(storeDto.getContractEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        storeRepository.save(store);

        return mapTodo(store);
    }

    // dùng để tìm kiếm store
    @Override
    public List<StoreDto> getStoreByNameContaining(String name) {
        List<Store> store = storeRepository.findStoresByStoreNameContaining(name);
        if (store.isEmpty()) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        return store.stream().map(this::mapTodo).toList();
    }

    // dùng để add vào staff và store stock
//    @Override
//    public StoreDto getStoreByName(String storeName) {
//        Store store = storeRepository.findStoreByStoreName(storeName);
//        if (store == null) {
//            throw new AppException(ErrorCode.STORE_NOT_EXIST);
//        }
//        return mapTodo(store);
//    }

    // dùng để add store vào staff
    @Override
    public Store getStoreEntityByName(String storeName) {
        Store store = storeRepository.findStoreByStoreName(storeName);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        return store;
    }

    @Override
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll().stream().map(this::mapTodo).toList();
    }

    @Override
    public StoreDto updateStore(int storeId, StoreDto storeDto) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        if (storeRepository.existsByStoreName(storeDto.getStoreName())) {
            throw new AppException(ErrorCode.STORE_EXISTED);
        }

        if (storeDto.getContractEndDate().isBefore(storeDto.getContractStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        if (storeDto.getContractEndDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_END_DATE_TIME);
        }

        if (storeDto.getStoreName() != null && !storeDto.getStoreName().trim().isEmpty()) {
            store.setStoreName(storeDto.getStoreName());
        }

        if (storeDto.getAddress() != null && !storeDto.getAddress().trim().isEmpty()) {
            store.setAddress(storeDto.getAddress());
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

        return mapTodo(store);
    }

    @Override
    public void deleteStore(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        storeRepository.delete(store);
    }

    private StoreDto mapTodo(Store store) {
        return StoreDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .phone(store.getPhone())
                .provinceName(store.getProvinceName())
                .ownerName(store.getOwnerName())
                .status(store.getStatus())
                .contractStartDate(store.getContractStartDate())
                .contractEndDate(store.getContractEndDate())
                .build();
    }
}
