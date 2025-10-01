package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "feedback_details")
public class FeedbackDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int feedbackDetailId;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String category;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String content;

    @ManyToOne
    @JoinColumn(name = "feedbackId", nullable = false)
    private Feedback feedback;
}
