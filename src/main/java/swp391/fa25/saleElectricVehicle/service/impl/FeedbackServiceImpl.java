package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Feedback;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDto;
import swp391.fa25.saleElectricVehicle.repository.FeedbackRepository;
import swp391.fa25.saleElectricVehicle.service.FeedbackService;
import swp391.fa25.saleElectricVehicle.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrderService orderService;

    @Override
    public FeedbackDto createFeedback(FeedbackDto dto) {
        Order order = orderService.getOrderEntityById(dto.getOrderId());

        Feedback feedback = Feedback.builder()
                .status(Feedback.FeedbackStatus.valueOf(dto.getStatus()))
                .createdAt(LocalDateTime.now())
                .createBy(dto.getCreateBy())
                .order(order)
                .build();

        Feedback saved = feedbackRepository.save(feedback);
        return mapToDto(saved);
    }

    @Override
    public FeedbackDto getFeedbackById(int feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        return mapToDto(feedback);
    }

    @Override
    public List<FeedbackDto> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDto> getFeedbacksByStatus(String status) {
        Feedback.FeedbackStatus feedbackStatus = Feedback.FeedbackStatus.valueOf(status);
        return feedbackRepository.findByStatus(feedbackStatus).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDto> getFeedbacksByOrder(int orderId) {
        return feedbackRepository.findByOrder_OrderId(orderId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackDto updateFeedback(int feedbackId, FeedbackDto dto) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        if (dto.getStatus() != null) {
            feedback.setStatus(Feedback.FeedbackStatus.valueOf(dto.getStatus()));
        }
        if (dto.getResolveAt() != null) {
            feedback.setResolveAt(dto.getResolveAt());
        }
        if (dto.getResolveBy() != null) {
            feedback.setResolveBy(dto.getResolveBy());
        }
        feedbackRepository.save(feedback);
        return mapToDto(feedback);
    }

    @Override
    public void deleteFeedback(int feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        feedbackRepository.delete(feedback);
    }

    private FeedbackDto mapToDto(Feedback feedback) {
        return FeedbackDto.builder()
                .feedbackId(feedback.getFeedbackId())
                .status(feedback.getStatus().name())
                .createdAt(feedback.getCreatedAt())
                .createBy(feedback.getCreateBy())
                .resolveAt(feedback.getResolveAt())
                .resolveBy(feedback.getResolveBy())
                .orderId(feedback.getOrder() != null ? feedback.getOrder().getOrderId() : 0)
                .build();
    }
}
