package swp391.fa25.saleElectricVehicle.entity.entity_enum;

public enum OrderStatus {
    DRAFT, // tạo nháp đơn hàng
    PENDING,
    CONFIRMED, // sau khi xác nhận đơn hàng
    CONTRACT_PENDING, // chờ ký hợp đồng
    CONTRACT_SIGNED, // sau khi ký hợp đồng
    DEPOSIT_PAID, // sau khi thanh toán đặt cọc
    FULLY_PAID, // sau khi thanh toán đầy đủ
    DELIVERED, // sau khi giao hàng
    CANCELLED // đơn hàng bị hủy
}
