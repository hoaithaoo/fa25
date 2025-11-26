package swp391.fa25.saleElectricVehicle.entity.entity_enum;

public enum OrderStatus {
    DRAFT, // tạo nháp đơn hàng
    PENDING,
    CONFIRMED, // sau khi xác nhận đơn hàng
    PENDING_DEPOSIT, // đã gán xe, chờ thanh toán đặt cọc
    DEPOSIT_PAID, // sau khi thanh toán đặt cọc
    DEPOSIT_SIGNED, // sau khi ký hợp đồng
    FULLY_PAID, // sau khi thanh toán đầy đủ
    SALE_SIGNED, // sau khi ký hợp đồng mua bán
    DELIVERED, // sau khi giao hàng
    EXPIRED, // đơn hàng hết hạn thanh toán
    CANCELLED // đơn hàng bị hủy
}
