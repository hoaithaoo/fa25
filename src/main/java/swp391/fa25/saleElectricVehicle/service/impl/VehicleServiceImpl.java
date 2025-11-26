package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.Vehicle;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.VehicleDto;
import swp391.fa25.saleElectricVehicle.repository.VehicleRepository;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionService;
import swp391.fa25.saleElectricVehicle.service.StoreService;
import swp391.fa25.saleElectricVehicle.service.UserService;
import swp391.fa25.saleElectricVehicle.service.VehicleService;
import swp391.fa25.saleElectricVehicle.utils.ExcelHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private InventoryTransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Override
    @Transactional
    public List<VehicleDto> importVehicles(MultipartFile file, int transactionId) {
        // 1. Kiểm tra định dạng file
        if (!ExcelHelper.hasExcelFormat(file)) {
            throw new RuntimeException("Vui lòng upload file Excel (.xlsx)!");
        }

        // 2. Lấy thông tin Đơn nhập hàng
        InventoryTransaction transaction = transactionService.getInventoryTransactionEntityById(transactionId);

        // 3. Kiểm tra trạng thái thanh toán - chỉ cho phép import khi PAYMENT_CONFIRMED
        // if (transaction.getStatus() != InventoryTransactionStatus.PAYMENT_CONFIRMED) {
        //     throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_IMPORT_VEHICLES);
        // }

        try {
            // 4. Parse Excel thành List Object
            List<Vehicle> vehicleList = ExcelHelper.excelToVehicles(file.getInputStream());

            // 5. Validate Số lượng (Quan trọng)
            // Nếu đơn đặt 5 xe, mà file excel có 6 dòng -> Báo lỗi
            if (vehicleList.size() != transaction.getImportQuantity()) {
                throw new RuntimeException("Số lượng trong Excel (" + vehicleList.size() +
                        ") không khớp với số lượng trong đơn hàng (" + transaction.getImportQuantity() + ")!");
            }

            // 6. Gán thông tin liên kết (Mapping)
            for (Vehicle v : vehicleList) {
                // Link xe này với đơn nhập hàng hiện tại
                v.setInventoryTransaction(transaction);

                // Link xe này với Model/Color (Stock) của đơn hàng
                // (Giả sử trong transaction có lưu stockId hoặc relationship tới Stock)
//                v.setStock(transaction.getStock());

                // Update ngày nhập là ngày hiện tại
//                v.setImportDate(java.time.LocalDateTime.now());
            }

            // 7. Lưu tất cả vào DB (Batch Insert)
            vehicleRepository.saveAll(vehicleList);

            // 8. Cập nhật trạng thái đơn nhập -> Đang vận chuyển
//            transactionService.startShipping(transactionId);
            // transaction.setStatus(InventoryTransactionStatus.SHIPPING);
            // transactionRepository.save(transaction);

            return vehicleList.stream().map(this::mapToDto).toList();

        } catch (IOException e) {
            throw new RuntimeException("Lỗi nhập dữ liệu Excel: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Dữ liệu trong file Excel bị trùng hoặc không hợp lệ: " + e.getMessage());
        }
    }

    @Override
    public VehicleDto getVehicleById(long vehicleId) {
        Vehicle vehicle = getVehicleEntityById(vehicleId);
        return mapToDto(vehicle);
    }

    @Override
    public Vehicle getVehicleEntityById(long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) {
            throw new AppException(ErrorCode.VEHICLE_NOT_FOUNDl);
        }
        return vehicle;
    }

    // Lấy danh sách vehicle trong inventory transaction
    @Override
    public List<VehicleDto> getVehiclesByInventoryTransaction(int inventoryId) {
        InventoryTransaction inventoryTransaction = transactionService.getInventoryTransactionEntityById(inventoryId);
        return inventoryTransaction.getVehicles().stream()
                .map(this::mapToDto)
                .toList();
    }

    // cập nhật trạng thái tất cả xe trong inventory transaction
    @Override
    public void updateVehicleStatus(int inventoryId, VehicleStatus status) {
        InventoryTransaction inventoryTransaction = transactionService.getInventoryTransactionEntityById(inventoryId);
        for (Vehicle vehicle : inventoryTransaction.getVehicles()) {
            vehicle.setStatus(status);
        }
    }

    @Override
    public void updateVehicleStatusById(long vehicleId, VehicleStatus status) {
        Vehicle vehicle = getVehicleEntityById(vehicleId);
        vehicle.setStatus(status);
        vehicleRepository.save(vehicle);
    }

    @Override
    public void updateVehicleDate(int inventoryId, VehicleStatus status, LocalDateTime date) {
        InventoryTransaction inventoryTransaction = transactionService.getInventoryTransactionEntityById(inventoryId);
        for (Vehicle vehicle : inventoryTransaction.getVehicles()) {
            if (status == VehicleStatus.SOLED) {
                vehicle.setSaleDate(date);
            } else if (status == VehicleStatus.AVAILABLE) {
                vehicle.setImportDate(date);
            }
        }
    }

    @Override
    public VehicleDto updateVehicleNote(int vehicleId, String note) {
        Vehicle vehicle = vehicleRepository.findById((long) vehicleId).orElse(null);
        if (vehicle == null) {
            throw new AppException(ErrorCode.VEHICLE_NOT_FOUNDl);
        }
        // Validate note
        if (note != null && !note.trim().isEmpty()) {
            if (note.length() > 100) {
                throw new AppException(ErrorCode.VEHICLE_NOTE_TOO_LONG);
            }
            vehicle.setNotes(note);
        }
        vehicleRepository.save(vehicle);
        return mapToDto(vehicle);
    }

    @Override
    public List<VehicleDto> getAvailableVehiclesByModelAndColor(int modelId, int colorId) {
        // Lấy user hiện tại
        User currentUser = userService.getCurrentUserEntity();
        
        // Lấy store hiện tại của user
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        // Lấy danh sách vehicles theo store, model, color và status = AVAILABLE, sắp xếp theo importDate ASC
        List<Vehicle> vehicles = vehicleRepository
                .findByStoreStock_Store_StoreIdAndStoreStock_ModelColor_Model_ModelIdAndStoreStock_ModelColor_Color_ColorIdAndStatusOrderByImportDateAsc(
                        currentStore.getStoreId(), modelId, colorId, VehicleStatus.AVAILABLE);
        
        return vehicles.stream()
                .map(this::mapToDto)
                .toList();
    }


    private VehicleDto mapToDto(Vehicle vehicle) {
        return VehicleDto.builder()
                .vehicleId(vehicle.getVehicleId())
                .vin(vehicle.getVin())
                .engineNo(vehicle.getEngineNo())
                .batteryNo(vehicle.getBatteryNo())
                .status(vehicle.getStatus().name())
                .importDate(vehicle.getImportDate())
                .saleDate(vehicle.getSaleDate())
                .notes(vehicle.getNotes())
                .inventoryTransaction(vehicle.getInventoryTransaction().getInventoryId())
                .build();
    }
}
