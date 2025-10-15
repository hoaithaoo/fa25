package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDto;

import java.util.List;

public interface FeedbackService {
    FeedbackDto createFeedback(FeedbackDto dto);
    FeedbackDto getFeedbackById(int feedbackId);
    List<FeedbackDto> getAllFeedbacks();
    List<FeedbackDto> getFeedbacksByStatus(String status);
    List<FeedbackDto> getFeedbacksByOrder(int orderId);
    FeedbackDto updateFeedback(int feedbackId, FeedbackDto dto);
    void deleteFeedback(int feedbackId);
}

