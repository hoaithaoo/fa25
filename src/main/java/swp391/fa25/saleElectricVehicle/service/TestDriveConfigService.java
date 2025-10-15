package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.TestDriveConfigDto;

import java.util.List;

public interface TestDriveConfigService {
    TestDriveConfigDto createTestDriveConfig(TestDriveConfigDto dto);
    TestDriveConfigDto getTestDriveConfigById(int configId);
    List<TestDriveConfigDto> getAllTestDriveConfigs();
    TestDriveConfigDto getTestDriveConfigByStore(int storeId);
    TestDriveConfigDto updateTestDriveConfig(int configId, TestDriveConfigDto dto);
    void deleteTestDriveConfig(int configId);
}
