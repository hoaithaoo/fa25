package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.FeedbackCategory;

@Entity
@Table(name = "feedback_details")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedbackDetailId;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private FeedbackCategory category;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "nvarchar(255)")
    private String content;

    @ManyToOne
    @JoinColumn(name = "feedbackId", nullable = false)
    private Feedback feedback;
}
