package swp391.fa25.saleElectricVehicle.payload.request.feedback;

import jakarta.validation.constraints.NotNull;
import lombok.*;

// ✅ DTO mới dành riêng cho Create - chỉ chứa field cần thiết khi tạo
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFeedbackRequest {

    @NotNull(message = "Order ID không được để trống")
    private Integer orderId; // ✅ Bắt buộc nhập

    @NotNull(message = "Tên khách hàng không được để trống")
    private String customerName; // ✅ Thêm field mới để lưu tên khách hàng

    private String status; // ✅ Không bắt buộc, default "PENDING" trong service

    // ❌ KHÔNG có feedbackId (auto-increment)
    // ❌ KHÔNG có createdAt (backend tự gán)
    // ❌ KHÔNG có createBy (backend lấy từ JWT)
    // ❌ KHÔNG có resolveAt, resolveBy (mặc định null)
}
