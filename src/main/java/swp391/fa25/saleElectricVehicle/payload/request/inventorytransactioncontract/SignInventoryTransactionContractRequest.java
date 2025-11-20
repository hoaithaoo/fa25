package swp391.fa25.saleElectricVehicle.payload.request.inventorytransactioncontract;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInventoryTransactionContractRequest {
    private String evmSignatureImageUrl; // URL ảnh chữ ký EVM từ Cloudinary
}

