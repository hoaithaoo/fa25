package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.FeedbackStatus;

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

//    // ✅ THÊM MỚI: Lưu tên khách hàng
//    @Column(nullable = false, columnDefinition = "nvarchar(255)")
//    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime resolveAt;

    @ManyToOne
    @JoinColumn(name = "resolvedBy")
    private User resolvedBy;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @OneToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackDetail> feedbackDetails = new java.util.ArrayList<>();
}
