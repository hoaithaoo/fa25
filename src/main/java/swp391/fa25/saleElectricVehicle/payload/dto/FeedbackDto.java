package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private int feedbackId;
    private String status; // Enum dưới dạng String
    private LocalDateTime createdAt;
    private String createBy;
    private LocalDateTime resolveAt;
    private String resolveBy;
    private int orderId; // Lưu orderId liên kết
}
