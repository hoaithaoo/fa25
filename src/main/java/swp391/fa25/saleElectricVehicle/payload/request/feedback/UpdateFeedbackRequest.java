package swp391.fa25.saleElectricVehicle.payload.request.feedback;

import lombok.*;

// ✅ DTO mới dành riêng cho Update - chỉ cho phép update status
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFeedbackRequest {

    private String status; // ✅ Chỉ cho phép update status

    // ❌ KHÔNG cho phép update orderId (không nên thay đổi)
    // ❌ KHÔNG cho phép update feedbackId, createdAt, createBy
    // ❌ resolveAt và resolveBy sẽ tự động gán khi status = "RESOLVED"
}
