package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "feedback_details")
public class FeedbackDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_detail_id")
    private int feedbackDetailId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String rating;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;
}
