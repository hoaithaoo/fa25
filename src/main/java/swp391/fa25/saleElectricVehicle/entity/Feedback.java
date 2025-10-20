package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "feedbacks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedbackId;

    // ✅ THÊM MỚI: Lưu tên khách hàng
    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String createBy;

    @Column
    private LocalDateTime resolveAt;

    @Column(columnDefinition = "nvarchar(255)")
    private String resolveBy;

    public enum FeedbackStatus {
        PENDING,
        IN_PROGRESS,
        RESOLVED,
        REJECTED
    }

    @OneToOne
    @JoinColumn(name = "order_id") // ✅ SỬA: "ordeId" → "order_id" (đúng naming convention)
    private Order order;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackDetail> feedbackDetails = new java.util.ArrayList<>();
}
