package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.TestDriveConfig;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.TestDriveConfigDto;
import swp391.fa25.saleElectricVehicle.repository.TestDriveConfigRepository;
import swp391.fa25.saleElectricVehicle.service.StoreService;
import swp391.fa25.saleElectricVehicle.service.TestDriveConfigService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestDriveConfigServiceImpl implements TestDriveConfigService {

    @Autowired
    private TestDriveConfigRepository testDriveConfigRepository;

    @Autowired
    private StoreService storeService;

    @Override
    public TestDriveConfigDto createTestDriveConfig(TestDriveConfigDto dto) {
        Store store = storeService.getStoreEntityById(dto.getStoreId());

        TestDriveConfig config = TestDriveConfig.builder()
                .maxAppointmentsPerDay(dto.getMaxAppointmentsPerDay())
                .appointmentDurationMinutes(dto.getAppointmentDurationMinutes())
                .maxConcurrentAppointments(dto.getMaxConcurrentAppointments())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .store(store)
                .build();

        TestDriveConfig saved = testDriveConfigRepository.save(config);
        return mapToDto(saved);
    }

    @Override
    public TestDriveConfigDto getTestDriveConfigById(int configId) {
        TestDriveConfig config = testDriveConfigRepository.findById(configId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_DRIVE_CONFIG_NOT_FOUND));
        return mapToDto(config);
    }

    @Override
    public List<TestDriveConfigDto> getAllTestDriveConfigs() {
        return testDriveConfigRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TestDriveConfigDto updateTestDriveConfig(int configId, TestDriveConfigDto dto) {
        TestDriveConfig config = testDriveConfigRepository.findById(configId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_DRIVE_CONFIG_NOT_FOUND));

        if (dto.getMaxAppointmentsPerDay() != 0) config.setMaxAppointmentsPerDay(dto.getMaxAppointmentsPerDay());
        if (dto.getAppointmentDurationMinutes() != 0) config.setAppointmentDurationMinutes(dto.getAppointmentDurationMinutes());
        if (dto.getMaxConcurrentAppointments() != 0) config.setMaxConcurrentAppointments(dto.getMaxConcurrentAppointments());
        if (dto.getStartTime() != null) config.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) config.setEndTime(dto.getEndTime());
        if (dto.getStoreId() != 0) {
            Store store = storeService.getStoreEntityById(dto.getStoreId());
            config.setStore(store);
        }

        TestDriveConfig updated = testDriveConfigRepository.save(config);
        return mapToDto(updated);
    }

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
                .maxAppointmentsPerDay(config.getMaxAppointmentsPerDay())
                .appointmentDurationMinutes(config.getAppointmentDurationMinutes())
                .maxConcurrentAppointments(config.getMaxConcurrentAppointments())
                .startTime(config.getStartTime())
                .endTime(config.getEndTime())
                .storeId(config.getStore().getStoreId())
                .build();
    }
}
