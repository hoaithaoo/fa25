package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.entity.Store;
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
import swp391.fa25.saleElectricVehicle.service.OrderService;
import swp391.fa25.saleElectricVehicle.service.OrderDetailService;
import swp391.fa25.saleElectricVehicle.entity.OrderDetail;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.payload.request.order.VehicleAssignment;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.utils.ExcelHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    @Lazy
    private OrderService orderService;

    @Autowired
//    @Lazy
    private OrderDetailService orderDetailService;

    // Import vehicles từ file Excel vào đơn nhập hàng
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
        if (inventoryTransaction.getVehicles() == null) {
            return new ArrayList<>();
        }
        return inventoryTransaction.getVehicles().stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .toList();
    }

    // cập nhật trạng thái tất cả xe trong inventory transaction
    @Override
    public void updateVehicleStatus(int inventoryId, VehicleStatus status) {
        InventoryTransaction inventoryTransaction = transactionService.getInventoryTransactionEntityById(inventoryId);
        if (inventoryTransaction.getVehicles() != null) {
            for (Vehicle vehicle : inventoryTransaction.getVehicles()) {
                if (vehicle != null) {
                    vehicle.setStatus(status);
                }
            }
        }
    }

    // cập nhật trạng thái xe theo vehicleId
    @Override
    public void updateVehicleStatusById(long vehicleId, VehicleStatus status) {
        Vehicle vehicle = getVehicleEntityById(vehicleId);
        vehicle.setStatus(status);
        vehicleRepository.save(vehicle);
    }

    // cập nhật ngày nhập hoặc ngày bán cho tất cả xe trong inventory transaction
    @Override
    public void updateVehicleDate(int inventoryId, VehicleStatus status, LocalDateTime date) {
        InventoryTransaction inventoryTransaction = transactionService.getInventoryTransactionEntityById(inventoryId);
        if (inventoryTransaction.getVehicles() != null) {
            for (Vehicle vehicle : inventoryTransaction.getVehicles()) {
                if (vehicle != null) {
                    if (status == VehicleStatus.SOLED) {
                        vehicle.setSaleDate(date);
                    } else if (status == VehicleStatus.AVAILABLE) {
                        vehicle.setImportDate(date);
                    }
                }
            }
        }
    }

    // cập nhật ghi chú xe
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

    // Lấy danh sách xe có sẵn theo model và color cho cửa hàng hiện tại
//    @Override
//    public List<VehicleDto> getAvailableVehiclesByModelAndColor(int modelId, int colorId) {
//        // Lấy user hiện tại
//        User currentUser = userService.getCurrentUserEntity();
//
//        // Lấy store hiện tại của user
//        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
//
//        // Lấy danh sách vehicles theo store, model, color và status = AVAILABLE, sắp xếp theo importDate ASC
//        List<Vehicle> vehicles = vehicleRepository
//                .findByStoreStock_Store_StoreIdAndStoreStock_ModelColor_Model_ModelIdAndStoreStock_ModelColor_Color_ColorIdAndStatusOrderByImportDateAsc(
//                        currentStore.getStoreId(), modelId, colorId, VehicleStatus.AVAILABLE);
//
//        return vehicles.stream()
//                .map(this::mapToDto)
//                .toList();
//    }

    @Override
    public List<Vehicle> getAvailableVehicleEntitiesByModelAndColor(int storeId, int modelId, int colorId) {
        // Lấy danh sách vehicles theo store, model, color và status = AVAILABLE, sắp xếp theo importDate ASC
        return vehicleRepository
                .findByStoreStock_Store_StoreIdAndStoreStock_ModelColor_Model_ModelIdAndStoreStock_ModelColor_Color_ColorIdAndStatusOrderByImportDateAsc(
                        storeId, modelId, colorId, VehicleStatus.AVAILABLE);
    }

    @Override
    public void updateVehicleWithOrderDetail(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public List<GetOrderDetailsResponse> assignVehiclesToOrderDetails(
            List<VehicleAssignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_NUMBER, "Danh sách gán xe không được rỗng");
        }
        
        // Load store và data
        Store currentStore = storeService.getCurrentStoreEntity(userService.getCurrentUserEntity().getUserId());
        Map<Integer, OrderDetail> orderDetailMap = new HashMap<>();
        Map<Long, Vehicle> vehicleMap = new HashMap<>();
        Map<Long, Integer> vehicleUsageMap = new HashMap<>();
        
        // Load order details và vehicles, validate store
        for (VehicleAssignment assignment : assignments) {
            int orderDetailId = assignment.getOrderDetailId();
            long vehicleId = assignment.getVehicleId();
            
            // Load order detail
            if (!orderDetailMap.containsKey(orderDetailId)) {
                OrderDetail orderDetail = orderDetailService.getOrderDetailEntityById(orderDetailId);
                if (orderDetail.getOrder().getStore().getStoreId() != currentStore.getStoreId()) {
                    throw new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND);
                }
                orderDetailMap.put(orderDetailId, orderDetail);
            }
            
            // Load vehicle
            if (!vehicleMap.containsKey(vehicleId)) {
                Vehicle vehicle = getVehicleEntityById(vehicleId);
                vehicleMap.put(vehicleId, vehicle);
            }
            
            // Validate không trùng vehicle
            if (vehicleUsageMap.containsKey(vehicleId)) {
                Vehicle vehicle = vehicleMap.get(vehicleId);
                String vin = vehicle.getVin() != null ? vehicle.getVin() : "N/A";
                throw new AppException(ErrorCode.VEHICLE_ALREADY_ASSIGNED, 
                    String.format("Xe có VIN %s (ID: %d) được gán cho nhiều order detail. Đã gán cho detail %d, không thể gán cho detail %d",
                        vin, vehicleId, vehicleUsageMap.get(vehicleId), orderDetailId));
            }
            vehicleUsageMap.put(vehicleId, orderDetailId);
        }
        
        // Validate tất cả vehicles
        for (VehicleAssignment assignment : assignments) {
            OrderDetail orderDetail = orderDetailMap.get(assignment.getOrderDetailId());
            Vehicle vehicle = vehicleMap.get(assignment.getVehicleId());
            
            if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
                throw new AppException(ErrorCode.VEHICLE_NOT_AVAILABLE, 
                    String.format("Xe có VIN %s (ID: %d) không có sẵn. Trạng thái: %s",
                        vehicle.getVin(), vehicle.getVehicleId(), vehicle.getStatus()));
            }
            
            // Validate model/color match
            int orderModelId = orderDetail.getStoreStock().getModelColor().getModel().getModelId();
            int orderColorId = orderDetail.getStoreStock().getModelColor().getColor().getColorId();
            int vehicleModelId = vehicle.getStoreStock().getModelColor().getModel().getModelId();
            int vehicleColorId = vehicle.getStoreStock().getModelColor().getColor().getColorId();
            
            if (orderModelId != vehicleModelId || orderColorId != vehicleColorId) {
                String vin = vehicle.getVin() != null ? vehicle.getVin() : "N/A";
                String orderInfo = String.format("%s - %s", 
                    orderDetail.getStoreStock().getModelColor().getModel().getModelName(),
                    orderDetail.getStoreStock().getModelColor().getColor().getColorName());
                String vehicleInfo = String.format("%s - %s",
                    vehicle.getStoreStock().getModelColor().getModel().getModelName(),
                    vehicle.getStoreStock().getModelColor().getColor().getColorName());
                throw new AppException(ErrorCode.VEHICLE_NOT_MATCH, 
                    String.format("Xe có VIN %s (ID: %d) không khớp. Đơn hàng: %s, Xe: %s",
                        vin, vehicle.getVehicleId(), orderInfo, vehicleInfo));
            }
        }
        
        // Update order status
        Order order = orderDetailMap.values().iterator().next().getOrder();
        orderService.updateOrderStatus(order, OrderStatus.PENDING_DEPOSIT);
        
        // Gán vehicles vào order details và cập nhật collection trong memory
        for (VehicleAssignment assignment : assignments) {
            OrderDetail orderDetail = orderDetailMap.get(assignment.getOrderDetailId());
            Vehicle vehicle = vehicleMap.get(assignment.getVehicleId());
            assignVehicleToOrderDetail(vehicle, orderDetail);
            
            // Cập nhật collection vehicles trong memory (bidirectional relationship)
            if (orderDetail.getVehicles() == null) {
                orderDetail.setVehicles(new ArrayList<>());
            }
            if (!orderDetail.getVehicles().contains(vehicle)) {
                orderDetail.getVehicles().add(vehicle);
            }
        }
        
        // Update và build response cho từng order detail (mỗi detail chỉ update 1 lần)
        Set<Integer> processedOrderDetailIds = new HashSet<>();
        List<GetOrderDetailsResponse> results = new ArrayList<>();
        
        for (VehicleAssignment assignment : assignments) {
            int orderDetailId = assignment.getOrderDetailId();
            
            if (!processedOrderDetailIds.contains(orderDetailId)) {
                OrderDetail orderDetail = orderDetailMap.get(orderDetailId);
                orderDetail.setUpdatedAt(LocalDateTime.now());
                orderDetailService.updateOrderDetail(orderDetail);
                
                // Dùng trực tiếp orderDetail đã có trong memory (đã cập nhật collection vehicles)
                results.add(mapOrderDetailToResponse(orderDetail));
                
                processedOrderDetailIds.add(orderDetailId);
            }
        }
        
        return results;
    }

    // ========== HELPER METHODS ==========

    // gán từng vehicle vào order detail
//    @Override
    private void assignVehicleToOrderDetail(Vehicle vehicle, OrderDetail orderDetail) {
        // Validate vehicle status = AVAILABLE
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
//            String vin = vehicle.getVin() != null ? vehicle.getVin() : "N/A";
            throw new AppException(
                    ErrorCode.VEHICLE_NOT_AVAILABLE,
                    String.format("Xe có VIN %s không có sẵn. Trạng thái hiện tại: %s",
                            vehicle.getVin(), vehicle.getStatus()));
        }

        // Validate vehicle model và color khớp với order detail
//        int vehicleModelId = vehicle.getStoreStock().getModelColor().getModel().getModelId();
//        int vehicleColorId = vehicle.getStoreStock().getModelColor().getColor().getColorId();
//        int orderDetailModelId = orderDetail.getStoreStock().getModelColor().getModel().getModelId();
//        int orderDetailColorId = orderDetail.getStoreStock().getModelColor().getColor().getColorId();
//
//        if (vehicleModelId != orderDetailModelId || vehicleColorId != orderDetailColorId) {
//            String vehicleInfo = String.format("%s - %s",
//                vehicle.getStoreStock().getModelColor().getModel().getModelName(),
//                vehicle.getStoreStock().getModelColor().getColor().getColorName());
//            String orderDetailInfo = String.format("%s - %s",
//                orderDetail.getStoreStock().getModelColor().getModel().getModelName(),
//                orderDetail.getStoreStock().getModelColor().getColor().getColorName());
//            String vin = vehicle.getVin() != null ? vehicle.getVin() : "N/A";
//            throw new AppException(
//                ErrorCode.VEHICLE_NOT_MATCH,
//                String.format("Xe có VIN %s (ID: %d) không khớp với order detail. Xe: %s, Order detail: %s",
//                    vin, vehicle.getVehicleId(), vehicleInfo, orderDetailInfo));
//        }

        // Gán orderDetail vào vehicle
        vehicle.setOrderDetail(orderDetail);

        // Update vehicle status từ AVAILABLE -> HOLDING
        vehicle.setStatus(VehicleStatus.HOLDING);

        // Save vehicle
        vehicleRepository.save(vehicle);
    }
    
    private GetOrderDetailsResponse mapOrderDetailToResponse(OrderDetail od) {
        List<VehicleDto> vehicles = null;
        if (od.getVehicles() != null && !od.getVehicles().isEmpty()) {
            vehicles = od.getVehicles().stream()
                    .map(this::mapToDto)
                    .toList();
        }
        
        return GetOrderDetailsResponse.builder()
                .orderDetailId(od.getId())
                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                .unitPrice(od.getUnitPrice())
                .quantity(od.getQuantity())
                .licensePlateFee(od.getLicensePlateFee())
                .serviceFee(od.getServiceFee())
                .otherTax(od.getOtherTax())
                .otherFees(od.getServiceFee().add(od.getOtherTax()))
                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : null)
                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                .discountAmount(od.getDiscountAmount())
                .totalPrice(od.getTotalPrice())
                .vehicles(vehicles)
                .build();
    }

    private VehicleDto mapToDto(Vehicle vehicle) {
        return VehicleDto.builder()
                .vehicleId(vehicle.getVehicleId())
                .vin(vehicle.getVin())
                .engineNo(vehicle.getEngineNo())
                .batteryNo(vehicle.getBatteryNo())
                .status(vehicle.getStatus() != null ? vehicle.getStatus().name() : null)
                .importDate(vehicle.getImportDate())
                .saleDate(vehicle.getSaleDate())
                .notes(vehicle.getNotes())
                .inventoryTransaction(vehicle.getInventoryTransaction() != null
                        ? vehicle.getInventoryTransaction().getInventoryId() : 0)
                .build();
    }
}
