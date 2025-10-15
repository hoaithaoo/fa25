package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDetailDto;

import java.util.List;

public interface FeedbackDetailService {
    FeedbackDetailDto createFeedbackDetail(FeedbackDetailDto dto);
    FeedbackDetailDto getFeedbackDetailById(int feedbackDetailId);
    List<FeedbackDetailDto> getAllFeedbackDetails();
    List<FeedbackDetailDto> getFeedbackDetailsByFeedback(int feedbackId);
    FeedbackDetailDto updateFeedbackDetail(int feedbackDetailId, FeedbackDetailDto dto);
    void deleteFeedbackDetail(int feedbackDetailId);
}
