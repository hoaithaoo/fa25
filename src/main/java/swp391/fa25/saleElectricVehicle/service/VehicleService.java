package swp391.fa25.saleElectricVehicle.service;

import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.entity.Vehicle;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus;
import swp391.fa25.saleElectricVehicle.payload.dto.VehicleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleService {
    List<VehicleDto> importVehicles(MultipartFile file, int transactionId);
    VehicleDto getVehicleById(long vehicleId);
    List<VehicleDto> getVehiclesByInventoryTransaction(int inventoryId);

    void updateVehicleStatus(int inventoryId, VehicleStatus status);
    void updateVehicleDate(int inventoryId, VehicleStatus status, LocalDateTime date);
    VehicleDto updateVehicleNote(int vehicleId, String note);
}
