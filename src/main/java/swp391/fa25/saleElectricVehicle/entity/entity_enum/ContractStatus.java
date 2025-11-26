package swp391.fa25.saleElectricVehicle.entity.entity_enum;

public enum ContractStatus {
    DRAFT,
    PENDING,
    DEPOSIT_SIGNED, // sau khi ký hợp đồng
    DEPOSIT_PAID, // sau khi thanh toán đặt cọc
    FULLY_PAID,
    SALE_SIGNED, // sau khi thanh toán đầy đủ
    // COMPLETED, // sau khi giao hàng
    CANCELLED,
    EXPIRED
}