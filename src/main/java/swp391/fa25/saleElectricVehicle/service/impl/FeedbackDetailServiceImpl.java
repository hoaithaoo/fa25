package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Feedback;
import swp391.fa25.saleElectricVehicle.entity.FeedbackDetail;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDetailDto;
import swp391.fa25.saleElectricVehicle.repository.FeedbackDetailRepository;
import swp391.fa25.saleElectricVehicle.service.FeedbackDetailService;
import swp391.fa25.saleElectricVehicle.service.FeedbackService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackDetailServiceImpl implements FeedbackDetailService {

    @Autowired
    private FeedbackDetailRepository feedbackDetailRepository;

    @Autowired
    private FeedbackService feedbackService;

    @Override
    public FeedbackDetailDto createFeedbackDetail(FeedbackDetailDto dto) {
        Feedback feedback = feedbackService.getFeedbackEntityById(dto.getFeedbackId());

        FeedbackDetail detail = FeedbackDetail.builder()
                .category(dto.getCategory())
                .rating(dto.getRating())
                .content(dto.getContent())
                .feedback(feedback)
                .build();

        FeedbackDetail saved = feedbackDetailRepository.save(detail);
        return mapToDto(saved);
    }

    @Override
    public FeedbackDetailDto getFeedbackDetailById(int feedbackDetailId) {
        FeedbackDetail detail = feedbackDetailRepository.findById(feedbackDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_DETAIL_NOT_FOUND));
        return mapToDto(detail);
    }

    @Override
    public List<FeedbackDetailDto> getAllFeedbackDetails() {
        return feedbackDetailRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDetailDto> getFeedbackDetailsByFeedback(int feedbackId) {
        return feedbackDetailRepository.findByFeedback_FeedbackId(feedbackId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackDetailDto updateFeedbackDetail(int feedbackDetailId, FeedbackDetailDto dto) {
        FeedbackDetail detail = feedbackDetailRepository.findById(feedbackDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_DETAIL_NOT_FOUND));

        if (dto.getCategory() != null) detail.setCategory(dto.getCategory());
        if (dto.getRating() != 0) detail.setRating(dto.getRating());
        if (dto.getContent() != null) detail.setContent(dto.getContent());
        if (dto.getFeedbackId() != 0) {
            Feedback feedback = feedbackService.getFeedbackEntityById(dto.getFeedbackId());
            detail.setFeedback(feedback);
        }

        FeedbackDetail updated = feedbackDetailRepository.save(detail);
        return mapToDto(updated);
    }

    @Override
    public void deleteFeedbackDetail(int feedbackDetailId) {
        FeedbackDetail detail = feedbackDetailRepository.findById(feedbackDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_DETAIL_NOT_FOUND));
        feedbackDetailRepository.delete(detail);
    }

    // Helper method
    private FeedbackDetailDto mapToDto(FeedbackDetail detail) {
        return FeedbackDetailDto.builder()
                .feedbackDetailId(detail.getFeedbackDetailId())
                .category(detail.getCategory())
                .rating(detail.getRating())
                .content(detail.getContent())
                .feedbackId(detail.getFeedback().getFeedbackId())
                .build();
    }
}
