package swp391.fa25.saleElectricVehicle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.StoreStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.repository.StoreRepository;
import swp391.fa25.saleElectricVehicle.service.StoreService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StoreRepository storeRepository;

    @Override
    public StoreDto createStore(StoreDto storeDto) {
        // check store name đã tồn tại chưa
//        if (storeRepository.existsByStoreName(storeDto.getStoreName())) {
//            throw new AppException(ErrorCode.STORE_EXISTED);
//        }

        LocalDate nowDate = LocalDate.now();
        // ngày bắt đầu không được trước ngày hiện tại
        if (storeDto.getContractStartDate().isBefore(nowDate)) {
            throw new AppException(ErrorCode.INVALID_START_DATE);
        }

        // ngày kết thúc phải sau ngày bắt đầu
        if (storeDto.getContractEndDate().isBefore(storeDto.getContractStartDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        // ngày kết thúc phải sau ngày hiện tại
        if (storeDto.getContractEndDate().isBefore(nowDate)) {
            throw new AppException(ErrorCode.INVALID_END_DATE_TIME);
        }

        // check status dựa trên ngày hợp đồng
        StoreStatus status;
        // nếu ngày bắt đầu hợp đồng > now hoặc ngày kết thúc < now thì inactive
        if (nowDate.isBefore(storeDto.getContractStartDate()) || nowDate.isAfter(storeDto.getContractEndDate())) {
            status = StoreStatus.INACTIVE;
        } else {
            status = StoreStatus.ACTIVE;
        }

        Store store = Store.builder()
                .storeName(storeDto.getStoreName())
                .address(storeDto.getAddress())
                .phone(storeDto.getPhone())
                .provinceName(storeDto.getProvinceName())
                .ownerName(storeDto.getOwnerName())
                .status(status)
                .imagePath(storeDto.getImagePath())
                .contractStartDate(storeDto.getContractStartDate())
                .contractEndDate(storeDto.getContractEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        storeRepository.save(store);

        return mapTodo(store);
    }

    @Override
    public StoreDto addStoreImagePath(int storeId, String imagePath) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        store.setImagePath(imagePath);
        store.setUpdatedAt(LocalDateTime.now());
        storeRepository.save(store);
        return mapTodo(store);
    }

    @Override
    public StoreDto getStoreById(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        return mapTodo(store);
    }

    @Override
    public Store getStoreEntityById(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        return store;
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


    // dùng để lấy store hiện tại của user
    @Override
    public Store getCurrentStoreEntity(int userId) {
        Store store = storeRepository.findStoreByUser_UserId(userId);
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
    public List<StoreDto> getAllActiveStores() {
        List<Store> stores = storeRepository.findStoresByStatus(StoreStatus.ACTIVE);
        return stores.stream().map(this::mapTodo).toList();
    }

    @Override
    public StoreDto updateStore(int storeId, StoreDto storeDto) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

//        if (storeDto.getStoreName() != null
//                && !storeDto.getStoreName().trim().isEmpty()
//                && !store.getStoreName().equals(storeDto.getStoreName()) &&
//                storeRepository.existsByStoreName(storeDto.getStoreName())) {
//            throw new AppException(ErrorCode.STORE_EXISTED);
//        }

        LocalDate nowDate = LocalDate.now();
        // ngày bắt đầu không được trước ngày hiện tại
        if (storeDto.getContractStartDate() != null
                && !storeDto.getContractStartDate().isEqual(store.getContractStartDate())) {
            if (storeDto.getContractStartDate().isBefore(nowDate)) {
                throw new AppException(ErrorCode.INVALID_START_DATE);
            }
            store.setContractStartDate(storeDto.getContractStartDate());
        }

        // ngày kết thúc phải sau ngày bắt đầu và sau ngày hiện tại
        if (storeDto.getContractEndDate() != null
                && !storeDto.getContractEndDate().isEqual(store.getContractEndDate())) {
            if (storeDto.getContractEndDate().isBefore(storeDto.getContractStartDate())) {
                throw new AppException(ErrorCode.INVALID_END_DATE);
            }
            if (storeDto.getContractEndDate().isBefore(nowDate)) {
                throw new AppException(ErrorCode.INVALID_END_DATE_TIME);
            }
            store.setContractEndDate(storeDto.getContractEndDate());
        }

        if (storeDto.getStoreName() != null
                && !storeDto.getStoreName().trim().isEmpty()
                && !store.getStoreName().equals(storeDto.getStoreName())) {
            store.setStoreName(storeDto.getStoreName());
        }

        if (storeDto.getAddress() != null
                && !storeDto.getAddress().trim().isEmpty()
                && !store.getAddress().equals(storeDto.getAddress())) {
            store.setAddress(storeDto.getAddress());
        }

        if (storeDto.getProvinceName() != null
                && !storeDto.getProvinceName().trim().isEmpty()
                && !store.getProvinceName().equals(storeDto.getProvinceName())) {
            store.setProvinceName(storeDto.getProvinceName());
        }

        if (storeDto.getOwnerName() != null
                && !storeDto.getOwnerName().trim().isEmpty()
                && !store.getOwnerName().equals(storeDto.getOwnerName())) {
            store.setOwnerName(storeDto.getOwnerName());
        }

        if (storeDto.getStatus() != null
                && store.getStatus() != storeDto.getStatus()) {
            store.setStatus(storeDto.getStatus());
        }

        if (storeDto.getImagePath() != null
                && !storeDto.getImagePath().trim().isEmpty()
                && !store.getImagePath().equals(storeDto.getImagePath())) {
            store.setImagePath(storeDto.getImagePath());
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
                .imagePath(store.getImagePath())
                .contractStartDate(store.getContractStartDate())
                .contractEndDate(store.getContractEndDate())
                .build();
    }

    // Runs every day at midnight (00:00:00)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void updateStoreContractStatus() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Checking store contract status at {}", now);

        // Deactivate stores with expired contracts (contractEndDate < now)
        int deactivated = storeRepository.deactivateStoresWithExpiredContracts(now);
        logger.info("Deactivated {} stores with expired contracts", deactivated);

        // Activate stores with valid contracts (contractStartDate <= now <= contractEndDate)
        int activated = storeRepository.activateStoresWithValidContracts(now);
        logger.info("Activated {} stores with valid contracts", activated);
    }
}
