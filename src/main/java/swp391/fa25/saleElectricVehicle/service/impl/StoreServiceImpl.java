package swp391.fa25.saleElectricVehicle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.StoreStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.payload.response.store.StoreMonthlyRevenueResponse;
import swp391.fa25.saleElectricVehicle.payload.response.store.TotalStoresMonthlyRevenueResponse;
import swp391.fa25.saleElectricVehicle.repository.StoreRepository;
import swp391.fa25.saleElectricVehicle.service.OrderService;
import swp391.fa25.saleElectricVehicle.service.StoreService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    @Lazy
    private OrderService orderService;

    @Override
    public StoreDto createStore(StoreDto storeDto) {

        LocalDate nowDate = LocalDate.now();
        // ngày bắt đầu có thể là ngày hôm nay hoặc sau, không được trước ngày hiện tại
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
        // ngày bắt đầu có thể là ngày hôm nay hoặc sau, không được trước ngày hiện tại
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
            // Lấy contractStartDate sau khi update (nếu có) hoặc từ store hiện tại
            LocalDate contractStartDate = store.getContractStartDate();
            if (contractStartDate == null && storeDto.getContractStartDate() != null) {
                contractStartDate = storeDto.getContractStartDate();
            }
            if (contractStartDate != null && storeDto.getContractEndDate().isBefore(contractStartDate)) {
                throw new AppException(ErrorCode.INVALID_END_DATE);
            }
            if (storeDto.getContractEndDate().isBefore(nowDate)) {
                throw new AppException(ErrorCode.INVALID_END_DATE_TIME);
            }
            store.setContractEndDate(storeDto.getContractEndDate());
        }

        // Luôn tự động cập nhật status dựa trên ngày hợp đồng, không cho phép cập nhật thủ công
        LocalDate contractStartDate = store.getContractStartDate();
        LocalDate contractEndDate = store.getContractEndDate();
        // nếu ngày bắt đầu hợp đồng > now hoặc ngày kết thúc < now thì inactive
        if (contractStartDate != null && contractEndDate != null) {
            if (nowDate.isBefore(contractStartDate) || nowDate.isAfter(contractEndDate)) {
                store.setStatus(StoreStatus.INACTIVE);
            } else {
                store.setStatus(StoreStatus.ACTIVE);
            }
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

    @Override
    @Transactional
    public StoreDto toggleStoreStatus(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        // Toggle status: nếu đang ACTIVE thì chuyển sang INACTIVE và ngược lại
        if (store.getStatus() == StoreStatus.ACTIVE) {
            store.setStatus(StoreStatus.INACTIVE);
        } else {
            store.setStatus(StoreStatus.ACTIVE);
        }

        store.setUpdatedAt(LocalDateTime.now());
        storeRepository.save(store);

        return mapTodo(store);
    }

    @Override
    public List<StoreMonthlyRevenueResponse> getMonthlyRevenueForAllStores() {
        // Lấy tháng hiện tại
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        // Lấy tất cả stores
        List<Store> stores = storeRepository.findAll();

        // Tính doanh thu cho từng store
        return stores.stream()
                .map(store -> {
                    // Lấy tất cả orders FULLY_PAID trong tháng hiện tại của store này
                    List<Order> fullyPaidOrders = orderService.getOrdersByStoreIdAndStatusAndDateRange(
                            store.getStoreId(),
                            OrderStatus.FULLY_PAID,
                            startOfMonth,
                            startOfNextMonth
                    );

                    // Tính tổng doanh thu (totalPayment của các orders)
                    BigDecimal monthlyRevenue = fullyPaidOrders.stream()
                            .map(Order::getTotalPayment)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return StoreMonthlyRevenueResponse.builder()
                            .storeId(store.getStoreId())
                            .storeName(store.getStoreName())
                            .address(store.getAddress())
                            .monthlyRevenue(monthlyRevenue)
                            .orderCount(fullyPaidOrders.size())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public TotalStoresMonthlyRevenueResponse getTotalStoresMonthlyRevenue(Integer year, Integer month) {
        // Nếu không có tham số, sử dụng tháng hiện tại
        YearMonth targetMonth;
        if (year != null && month != null) {
            // Validate month (1-12)
            if (month < 1 || month > 12) {
                throw new AppException(ErrorCode.INVALID_END_DATE); // Có thể tạo error code riêng sau
            }
            targetMonth = YearMonth.of(year, month);
        } else {
            targetMonth = YearMonth.now();
        }

        LocalDateTime startOfMonth = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

        // Lấy tất cả stores
        List<Store> stores = storeRepository.findAll();

        // Tính tổng đơn hàng và tổng doanh thu của tất cả store
        long totalOrders = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Store store : stores) {
            // Lấy tất cả orders FULLY_PAID trong tháng của store này
            List<Order> fullyPaidOrders = orderService.getOrdersByStoreIdAndStatusAndDateRange(
                    store.getStoreId(),
                    OrderStatus.FULLY_PAID,
                    startOfMonth,
                    startOfNextMonth
            );

            // Cộng dồn số đơn hàng
            totalOrders += fullyPaidOrders.size();

            // Cộng dồn doanh thu
            BigDecimal storeRevenue = fullyPaidOrders.stream()
                    .map(Order::getTotalPayment)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalRevenue = totalRevenue.add(storeRevenue);
        }

        return TotalStoresMonthlyRevenueResponse.builder()
                .year(targetMonth.getYear())
                .month(targetMonth.getMonthValue())
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .build();
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
                .createdAt(store.getCreatedAt())
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
