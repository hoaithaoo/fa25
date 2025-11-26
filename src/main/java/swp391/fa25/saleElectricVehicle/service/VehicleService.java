package swp391.fa25.saleElectricVehicle.service;

import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.entity.Vehicle;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus;
import swp391.fa25.saleElectricVehicle.payload.dto.VehicleDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.VehicleAssignment;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleService {
    List<VehicleDto> importVehicles(MultipartFile file, int transactionId);
    VehicleDto getVehicleById(long vehicleId);
    Vehicle getVehicleEntityById(long vehicleId);
    List<VehicleDto> getVehiclesByInventoryTransaction(int inventoryId);
//    List<VehicleDto> getAvailableVehiclesByModelAndColor(int modelId, int colorId);

    void updateVehicleStatus(int inventoryId, VehicleStatus status);
    void updateVehicleStatusById(long vehicleId, VehicleStatus status);
    void updateVehicleDate(int inventoryId, VehicleStatus status, LocalDateTime date);
    VehicleDto updateVehicleNote(int vehicleId, String note);
    
    // Lấy danh sách vehicle entities (không phải DTO) theo store, model, color và status
    List<Vehicle> getAvailableVehicleEntitiesByModelAndColor(int storeId, int modelId, int colorId);
    
    // Update vehicle với orderDetail và status
    void updateVehicleWithOrderDetail(Vehicle vehicle);
    
    // Assign vehicle vào order detail (validate và update vehicle)
//    void assignVehicleToOrderDetail(Vehicle vehicle, swp391.fa25.saleElectricVehicle.entity.OrderDetail orderDetail);
    
    // Assign nhiều vehicles vào order details (main method)
    List<GetOrderDetailsResponse> assignVehiclesToOrderDetails(List<VehicleAssignment> assignments);
}
