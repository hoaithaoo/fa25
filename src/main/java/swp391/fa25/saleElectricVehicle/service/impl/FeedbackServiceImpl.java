package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; // ✅ THÊM MỚI
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Feedback;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.FeedbackStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDetailDto;
import swp391.fa25.saleElectricVehicle.payload.dto.FeedbackDto;
import swp391.fa25.saleElectricVehicle.payload.request.feedback.CreateFeedbackRequest; // ✅ THÊM MỚI
import swp391.fa25.saleElectricVehicle.payload.request.feedback.UpdateFeedbackRequest; // ✅ THÊM MỚI
import swp391.fa25.saleElectricVehicle.repository.FeedbackRepository;
import swp391.fa25.saleElectricVehicle.service.FeedbackService;
import swp391.fa25.saleElectricVehicle.service.OrderService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // ✅ THAY ĐỔI: Dùng CreateFeedbackRequest thay vì FeedbackDto
    // tạo draft feedback
    @Override
    public FeedbackDto createFeedback(CreateFeedbackRequest request) {
        // ✅ Validate orderId có tồn tại không
        Order order = orderService.getOrderEntityById(request.getOrderId());

        // ✅ Lấy user đang login (từ JWT token)
//        String currentUser = SecurityContextHolder.getContext()
//                .getAuthentication().getName();
        User staff = userService.getCurrentUserEntity();

        // ✅ Tạo Feedback với giá trị tự động
        Feedback feedback = Feedback.builder()
                .order(order)
                .status(FeedbackStatus.DRAFT)
                .createdAt(LocalDateTime.now()) // ✅ Tự động gán thời gian tạo
                .createdBy(staff)
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

//    @Override
//    public List<FeedbackDto> getFeedbacksByStatus(String status) {
//        FeedbackStatus feedbackStatus = FeedbackStatus.valueOf(status);
//        return feedbackRepository.findByStatus(feedbackStatus).stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }

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
            feedback.setStatus(request.getStatus());
            if (FeedbackStatus.RESOLVED.equals(request.getStatus())
            || FeedbackStatus.REJECTED.equals(request.getStatus())) {
                feedback.setResolveAt(LocalDateTime.now());
                feedback.setResolvedBy(userService.getCurrentUserEntity());
            }

            // ✅ Nếu status = "RESOLVED" → tự động gán resolveAt và resolveBy
//            if ("RESOLVED".equals(request.getStatus())) {
//                feedback.setResolveAt(LocalDateTime.now());
//                feedback.setResolveBy(SecurityContextHolder.getContext()
//                        .getAuthentication().getName());
//            }
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
                .orderId(feedback.getOrder().getOrderId())
                .customerId(feedback.getOrder().getCustomer().getCustomerId())
                .customerName(feedback.getOrder().getCustomer().getFullName())
                .feedbackDetails(
                        feedback.getFeedbackDetails().stream()
                                .map(fd -> FeedbackDetailDto.builder()
                                        .feedbackDetailId(fd.getFeedbackDetailId())
                                        .category(fd.getCategory())
                                        .rating(fd.getRating())
                                        .content(fd.getContent())
                                        .build()
                                ).toList()
                )
                .status(feedback.getStatus().name())
                .createdAt(feedback.getCreatedAt())
                .createdById(feedback.getCreatedBy().getUserId())
                .createdBy(feedback.getCreatedBy().getFullName())
                .resolveAt(feedback.getResolveAt())
                .resolvedById(feedback.getResolvedBy().getUserId())
                .resolvedBy(feedback.getResolvedBy().getFullName())
                .build();
    }
}
