package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Feedback;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDto;
import swp391.fa25.saleElectricVehicle.payload.request.feedback.CreateFeedbackRequest; // ✅ THÊM MỚI
import swp391.fa25.saleElectricVehicle.payload.request.feedback.UpdateFeedbackRequest; // ✅ THÊM MỚI

import java.util.List;

public interface FeedbackService {
    // ✅ THAY ĐỔI: Dùng CreateFeedbackRequest
    FeedbackDto createFeedback(CreateFeedbackRequest request);

    FeedbackDto getFeedbackById(int feedbackId);
    List<FeedbackDto> getAllFeedbacks();
    List<FeedbackDto> getFeedbacksByOrder(int orderId);

    // ✅ THAY ĐỔI: Dùng UpdateFeedbackRequest
    FeedbackDto updateFeedback(int feedbackId, UpdateFeedbackRequest request);

    void deleteFeedback(int feedbackId);
    Feedback getFeedbackEntityById(int feedbackId);
}
