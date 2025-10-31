package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.FeedbackCategory;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDetailDto {
    private int feedbackDetailId;
    private int feedbackId; // Liên kết với Feedback
    private FeedbackCategory category;
    private int rating;
    private String content;
}
