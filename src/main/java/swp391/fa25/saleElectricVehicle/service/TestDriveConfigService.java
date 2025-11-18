package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.TestDriveConfig;
import swp391.fa25.saleElectricVehicle.payload.dto.TestDriveConfigDto;

public interface TestDriveConfigService {
    TestDriveConfigDto createTestDriveConfig(TestDriveConfigDto dto);
    TestDriveConfigDto getTestDriveConfig();
    TestDriveConfig getTestDriveConfigEntity(); // Method để lấy entity thay vì DTO
//    TestDriveConfigDto getTestDriveConfigByStoreId(int storeId);
//    List<TestDriveConfigDto> getAllTestDriveConfigs();
    TestDriveConfigDto updateTestDriveConfig(int configId, TestDriveConfigDto dto);
//    TestDriveConfigDto updateTestDriveConfigByStoreId(int storeId, TestDriveConfigDto dto);
    void deleteTestDriveConfig(Integer configId);
}
