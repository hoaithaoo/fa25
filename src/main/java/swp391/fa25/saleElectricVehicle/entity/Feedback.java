package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "feedbacks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedbackId;

    @Enumerated(EnumType.STRING)
    @Column
    private FeedbackStatus status;

    @Column
    private LocalDateTime createdAt;

    @Column
    private String createBy;

    @Column
    private LocalDateTime resolveAt;

    @Column
    private String resolveBy;

    public enum FeedbackStatus {
        PENDING,
        IN_PROGRESS,
        RESOLVED,
        REJECTED
    }

    @OneToOne
    @JoinColumn(name = "ordeId")
    private Order order;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackDetail> feedbackDetails = new java.util.ArrayList<>();
}