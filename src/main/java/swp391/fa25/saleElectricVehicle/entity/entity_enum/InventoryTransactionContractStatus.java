package swp391.fa25.saleElectricVehicle.entity.entity_enum;

public enum InventoryTransactionContractStatus {
    DRAFT,          // Hợp đồng mới tạo, chưa ký
    EVM_SIGNED,     // EVM đã ký, chờ Store ký
    SIGNED          // Cả 2 bên đã ký
}

