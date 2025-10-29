package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; // ✅ THÊM MỚI
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Feedback;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDto;
import swp391.fa25.saleElectricVehicle.payload.request.feedback.CreateFeedbackRequest; // ✅ THÊM MỚI
import swp391.fa25.saleElectricVehicle.payload.request.feedback.UpdateFeedbackRequest; // ✅ THÊM MỚI
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

    // ✅ THAY ĐỔI: Dùng CreateFeedbackRequest thay vì FeedbackDto
    @Override
    public FeedbackDto createFeedback(CreateFeedbackRequest request) {
        // ✅ Validate orderId có tồn tại không
        Order order = orderService.getOrderEntityById(request.getOrderId());

        // ✅ Lấy user đang login (từ JWT token)
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // ✅ Tạo Feedback với giá trị tự động
        Feedback feedback = Feedback.builder()
                .order(order)
                .customerName(request.getCustomerName()) // ✅ Lưu tên khách hàng
                .status(request.getStatus() != null
                        ? Feedback.FeedbackStatus.valueOf(request.getStatus())
                        : Feedback.FeedbackStatus.PENDING) // ✅ Default PENDING
                .createdAt(LocalDateTime.now()) // ✅ Tự động gán thời gian tạo
                .createBy(currentUser) // ✅ Tự động lấy từ JWT
                .resolveAt(null) // ✅ Mặc định null
                .resolveBy(null) // ✅ Mặc định null
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

    // ✅ THAY ĐỔI: Dùng UpdateFeedbackRequest thay vì FeedbackDto
    @Override
    public FeedbackDto updateFeedback(int feedbackId, UpdateFeedbackRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        // ✅ Update status nếu có
        if (request.getStatus() != null) {
            feedback.setStatus(Feedback.FeedbackStatus.valueOf(request.getStatus()));

            // ✅ Nếu status = "RESOLVED" → tự động gán resolveAt và resolveBy
            if ("RESOLVED".equals(request.getStatus())) {
                feedback.setResolveAt(LocalDateTime.now());
                feedback.setResolveBy(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
            }
        }

        Feedback updated = feedbackRepository.save(feedback);
        return mapToDto(updated);
    }

    @Override
    public void deleteFeedback(int feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        feedbackRepository.delete(feedback);
    }

    @Override
    public Feedback getFeedbackEntityById(int feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
    }

    // ✅ THÊM customerName vào mapping
    private FeedbackDto mapToDto(Feedback feedback) {
        return FeedbackDto.builder()
                .feedbackId(feedback.getFeedbackId())
                .customerName(feedback.getCustomerName()) // ✅ THÊM MỚI
                .status(feedback.getStatus().name())
                .createdAt(feedback.getCreatedAt())
                .createBy(feedback.getCreateBy())
                .resolveAt(feedback.getResolveAt())
                .resolveBy(feedback.getResolveBy())
                .orderId(feedback.getOrder() != null ? feedback.getOrder().getOrderId() : 0)
                .build();
    }
}
