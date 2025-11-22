package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.TestDriveConfig;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.TestDriveConfigDto;
import swp391.fa25.saleElectricVehicle.repository.TestDriveConfigRepository;
import swp391.fa25.saleElectricVehicle.service.StoreService;
import swp391.fa25.saleElectricVehicle.service.TestDriveConfigService;
import swp391.fa25.saleElectricVehicle.service.UserService;

@Service
public class TestDriveConfigServiceImpl implements TestDriveConfigService {

    @Autowired
    private TestDriveConfigRepository testDriveConfigRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    @Override
    public TestDriveConfigDto createTestDriveConfig(TestDriveConfigDto dto) {
        // Kiểm tra quyền: chỉ manager mới được tạo config
        User currentUser = userService.getCurrentUserEntity();
        if (!currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng")) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CREATE_TEST_DRIVE_CONFIG);
        }

        // Lấy store của user hiện tại
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());

        // Kiểm tra nếu store đã có config thì throw exception
        TestDriveConfig existingConfig = testDriveConfigRepository.findByStore_StoreId(currentStore.getStoreId());
        if (existingConfig != null) {
            throw new AppException(ErrorCode.TEST_DRIVE_CONFIG_EXISTED);
        }

        TestDriveConfig config = TestDriveConfig.builder()
                .appointmentDurationMinutes(dto.getAppointmentDurationMinutes())
                .maxAppointmentsPerModelPerSlot(dto.getMaxAppointmentsPerModelPerSlot())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .store(currentStore)
                .build();

        TestDriveConfig saved = testDriveConfigRepository.save(config);
        return mapToDto(saved);
    }

    @Override
    public TestDriveConfigDto getTestDriveConfig() {
        // Validate store exists
        User currentUser = userService.getCurrentUserEntity();
        
        TestDriveConfig config = testDriveConfigRepository.findByStore_StoreId(currentUser.getStore().getStoreId());
        if (config == null) {
            throw new AppException(ErrorCode.TEST_DRIVE_CONFIG_NOT_FOUND);
        }
        return mapToDto(config);
    }

    @Override
    public TestDriveConfig getTestDriveConfigEntity() {
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        TestDriveConfig config = testDriveConfigRepository.findByStore_StoreId(currentStore.getStoreId());
        if (config == null) {
            throw new AppException(ErrorCode.TEST_DRIVE_CONFIG_NOT_FOUND);
        }
        return config;
    }

    // @Override
    // public List<TestDriveConfigDto> getAllTestDriveConfigs() {
    //     return testDriveConfigRepository.findAll().stream()
    //             .map(this::mapToDto)
    //             .collect(Collectors.toList());
    // }

    @Override
    public TestDriveConfigDto updateTestDriveConfig(int configId, TestDriveConfigDto dto) {
        // Kiểm tra quyền: chỉ manager mới được update config
        User currentUser = userService.getCurrentUserEntity();
        if (!currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng")) {
            throw new AppException(ErrorCode.UNAUTHORIZED_UPDATE_TEST_DRIVE_CONFIG);
        }

        TestDriveConfig config = testDriveConfigRepository.findById(configId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_DRIVE_CONFIG_NOT_FOUND));

        // Manager chỉ có thể update config của store của chính họ
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        if (config.getStore().getStoreId() != currentStore.getStoreId()) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TEST_DRIVE_CONFIG_ACCESS);
        }

        // Update fields (không cho phép đổi store, storeId từ DTO sẽ bị bỏ qua)
        if (dto.getAppointmentDurationMinutes() != 0) config.setAppointmentDurationMinutes(dto.getAppointmentDurationMinutes());
        if (dto.getMaxAppointmentsPerModelPerSlot() != 0) config.setMaxAppointmentsPerModelPerSlot(dto.getMaxAppointmentsPerModelPerSlot());
        if (dto.getStartTime() != null) config.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) config.setEndTime(dto.getEndTime());
        // Không cho phép đổi store - store luôn là store của manager hiện tại

        TestDriveConfig updated = testDriveConfigRepository.save(config);
        return mapToDto(updated);
    }

    // @Override
    // public TestDriveConfigDto updateTestDriveConfigByStoreId(int storeId, TestDriveConfigDto dto) {
    //     // Kiểm tra quyền: chỉ manager mới được update config
    //     User currentUser = userService.getCurrentUserEntity();
    //     if (!currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng")) {
    //         throw new AppException(ErrorCode.UNAUTHORIZED_UPDATE_TEST_DRIVE_CONFIG);
    //     }

    //     // Lấy store của manager hiện tại (bỏ qua storeId từ parameter)
    //     Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
    //     // Get config by storeId của manager hiện tại
    //     TestDriveConfig config = testDriveConfigRepository.findByStore_StoreId(currentStore.getStoreId());
    //     if (config == null) {
    //         throw new AppException(ErrorCode.TEST_DRIVE_CONFIG_NOT_FOUND);
    //     }

    //     // Update fields (không cho phép đổi store)
    //     if (dto.getMaxAppointmentsPerDay() != 0) config.setMaxAppointmentsPerDay(dto.getMaxAppointmentsPerDay());
    //     if (dto.getAppointmentDurationMinutes() != 0) config.setAppointmentDurationMinutes(dto.getAppointmentDurationMinutes());
    //     if (dto.getMaxConcurrentAppointments() != 0) config.setMaxConcurrentAppointments(dto.getMaxConcurrentAppointments());
    //     if (dto.getStartTime() != null) config.setStartTime(dto.getStartTime());
    //     if (dto.getEndTime() != null) config.setEndTime(dto.getEndTime());
    //     // Không cho phép đổi store - store luôn là store của manager hiện tại

    //     TestDriveConfig updated = testDriveConfigRepository.save(config);
    //     return mapToDto(updated);
    // }

    @Override
    @Transactional
    public void deleteTestDriveConfig(Integer configId) {
        TestDriveConfig config = testDriveConfigRepository.findById(configId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_DRIVE_CONFIG_NOT_FOUND));
        Store store = config.getStore();
        if (store != null) {
            store.setTestDriveConfig(null); // Loại reference khỏi parent
            // Nếu có StoreRepository thì save lại store
            // storeRepository.save(store);
        }
    }


    private TestDriveConfigDto mapToDto(TestDriveConfig config) {
        return TestDriveConfigDto.builder()
                .configId(config.getConfigId())
                .appointmentDurationMinutes(config.getAppointmentDurationMinutes())
                .maxAppointmentsPerModelPerSlot(config.getMaxAppointmentsPerModelPerSlot())
                .startTime(config.getStartTime())
                .endTime(config.getEndTime())
                .storeId(config.getStore().getStoreId())
                .build();
    }
}
