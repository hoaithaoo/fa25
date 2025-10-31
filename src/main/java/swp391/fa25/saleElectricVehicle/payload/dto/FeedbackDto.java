package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ✅ DTO dùng cho Response - trả về đầy đủ thông tin
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private int feedbackId;
    private int orderId;

    private int customerId;
    private String customerName; // ✅ THÊM MỚI

    List<FeedbackDetailDto> feedbackDetails;

    private String status;
    private LocalDateTime createdAt;
    private int createdById;
    private String createdBy;
    private LocalDateTime resolveAt;
    private int resolvedById;
    private String resolvedBy;
}
