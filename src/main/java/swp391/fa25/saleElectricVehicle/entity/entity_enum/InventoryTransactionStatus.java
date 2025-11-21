package swp391.fa25.saleElectricVehicle.entity.entity_enum;

public enum InventoryTransactionStatus {
    PENDING,      // Đại lý tạo request, chờ hãng xử lý
    CONFIRMED,    // Hãng đã chấp nhận request
    EVM_SIGNED,  // Hãng đã ký hợp đồng, chờ đại lý ký
    CONTRACT_SIGNED, // Hợp đồng đã được ký bởi cả 2 bên
    REJECTED,     // Hãng đã từ chối request
    FILE_UPLOADED, // Biên lai than toán đã được tải lên, chờ xác nhận
    PAYMENT_CONFIRMED, // Hãng đã xác nhận thanh toán
    IN_TRANSIT,   // Đang giao hàng (sau khi CONFIRMED)
    DELIVERED,    // Đại lý xác nhận đã nhận hàng, cập nhật tồn kho
    CANCELLED     // Đại lý hủy request
}

