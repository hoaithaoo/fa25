package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.FeedbackDetail;

import java.util.List;

@Repository
public interface FeedbackDetailRepository extends JpaRepository<FeedbackDetail, Integer> {
    List<FeedbackDetail> findByFeedback_FeedbackId(int feedbackId);
}
