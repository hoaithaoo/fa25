package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Feedback;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
//    List<Feedback> findByStatus(Feedback.FeedbackStatus status);
    List<Feedback> findByOrder_OrderId(int orderId);

    // Find feedbacks by store (for manager)
    List<Feedback> findByOrder_Store_StoreId(int storeId);

    // Find feedbacks by store and user (for staff filtering)
    List<Feedback> findByOrder_Store_StoreIdAndOrder_User_UserId(int storeId, int userId);

    // Find feedback by store, user and feedbackId (for staff filtering)
    Feedback findByOrder_Store_StoreIdAndOrder_User_UserIdAndFeedbackId(int storeId, int userId, int feedbackId);
}
