package swp391.fa25.saleElectricVehicle.entity.entity_enum;

public enum VehicleStatus {
    IN_TRANSIT, // Xe đang vận chuyển
    AVAILABLE, // Xe có sẵn để bán
    HOLDING, // Xe đã được gán cho đơn hàng, chờ thanh toán đặt cọc
    SOLED, // Xe đã được bán
    DEFECTIVE, // Xe bị lỗi
    RETURNED // Xe đã được trả lại
}
