package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDetailDto {
    private int feedbackDetailId;
    private String category;
    private int rating;
    private String content;
    private int feedbackId; // Liên kết với Feedback
}
