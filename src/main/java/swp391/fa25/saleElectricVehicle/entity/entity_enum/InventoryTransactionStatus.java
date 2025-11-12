package swp391.fa25.saleElectricVehicle.entity.entity_enum;

public enum InventoryTransactionStatus {
    PENDING,      // Đại lý tạo request, chờ hãng xử lý
    CONFIRMED,    // Hãng đã chấp nhận request
    REJECTED,     // Hãng đã từ chối request
    IN_TRANSIT,   // Đang giao hàng (sau khi CONFIRMED)
    DELIVERED,    // Đại lý xác nhận đã nhận hàng, cập nhật tồn kho
    CANCELLED     // Đại lý hủy request
}

