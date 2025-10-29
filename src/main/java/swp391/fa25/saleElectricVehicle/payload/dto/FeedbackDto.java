package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

// ✅ DTO dùng cho Response - trả về đầy đủ thông tin
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private int feedbackId;
    private String customerName; // ✅ THÊM MỚI
    private String status;
    private LocalDateTime createdAt;
    private String createBy;
    private LocalDateTime resolveAt;
    private String resolveBy;
    private int orderId;
}
