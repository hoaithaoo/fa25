package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.TestDriveConfig;
import swp391.fa25.saleElectricVehicle.payload.dto.TestDriveConfigDto;

public interface TestDriveConfigService {
    TestDriveConfigDto createTestDriveConfig(TestDriveConfigDto dto);
    TestDriveConfigDto getTestDriveConfig();
    TestDriveConfig getTestDriveConfigEntity(); // Method để lấy entity thay vì DTO
    TestDriveConfigDto updateTestDriveConfig(int configId, TestDriveConfigDto dto);
    void deleteTestDriveConfig(Integer configId);
}
