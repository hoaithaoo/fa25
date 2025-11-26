package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetPaymentResponse;
import swp391.fa25.saleElectricVehicle.repository.PaymentRepository;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    @Lazy
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Override
    public GetPaymentResponse createDraftPayment(CreatePaymentRequest request) {
        // Lấy Order entity từ OrderService với @Lazy để tránh circular dependency
        Order order = orderService.getOrderEntityById(request.getOrderId());

        // Validate order status
        if (request.getPaymentType() == PaymentType.DEPOSIT) {
            // Payment deposit chỉ được tạo khi order ở trạng thái PENDING_DEPOSIT
            if (order.getStatus() != OrderStatus.PENDING_DEPOSIT) {
                throw new AppException(ErrorCode.ORDER_NOT_IN_CONFIRMED_STATUS, 
                    "Chỉ có thể tạo payment đặt cọc cho đơn hàng ở trạng thái PENDING_DEPOSIT");
            }
            
            // Kiểm tra xem đã có payment đặt cọc active (không phải CANCELLED hoặc DRAFT) chưa
            List<Payment> existingDepositPayments = paymentRepository
                    .findActivePaymentsByOrderAndPaymentType(
                            order, PaymentType.DEPOSIT, 
                            PaymentStatus.CANCELLED, PaymentStatus.DRAFT);
            // nếu đã có payment active thì không được tạo nữa
            if (!existingDepositPayments.isEmpty()) {
                throw new AppException(ErrorCode.DEPOSIT_PAYMENT_ALREADY_EXISTS);
            }
        } else if (request.getPaymentType() == PaymentType.BALANCE) {
            // Payment balance chỉ được tạo khi order ở trạng thái DEPOSIT_PAID hoặc DEPOSIT_SIGNED
            if (order.getStatus() != OrderStatus.DEPOSIT_PAID && order.getStatus() != OrderStatus.DEPOSIT_SIGNED) {
                throw new AppException(ErrorCode.ORDER_NOT_IN_CONFIRMED_STATUS, 
                    "Chỉ có thể tạo payment số dư cho đơn hàng đã thanh toán đặt cọc");
            }
            
            // kiểm tra xem đã có payment đặt cọc completed chưa
            List<Payment> existingDepositPayments = paymentRepository
                    .findActivePaymentsByOrderAndPaymentType(
                            order, PaymentType.DEPOSIT, 
                            PaymentStatus.CANCELLED, PaymentStatus.DRAFT);
            boolean hasCompletedDeposit = existingDepositPayments.stream()
                    .anyMatch(p -> p.getStatus().equals(PaymentStatus.COMPLETED));
            if (!hasCompletedDeposit) {
                throw new AppException(ErrorCode.DEPOSIT_PAYMENT_NOT_COMPLETED);
            }
            
            // Kiểm tra xem đã có payment thanh toán số dư active chưa
            List<Payment> existingBalancePayments = paymentRepository
                    .findActivePaymentsByOrderAndPaymentType(
                            order, PaymentType.BALANCE, 
                            PaymentStatus.CANCELLED, PaymentStatus.DRAFT);
            if (!existingBalancePayments.isEmpty()) {
                throw new AppException(ErrorCode.BALANCE_PAYMENT_ALREADY_EXISTS);
            }
        }

        // Tạo payment
        Payment payment = Payment.builder()
                .status(PaymentStatus.DRAFT)
                .paymentType(request.getPaymentType())
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .order(order)
                .build();

        // Save to get paymentId
        paymentRepository.save(payment);
        
        // Set payment code
        if (PaymentType.DEPOSIT.equals(request.getPaymentType())) {
            payment.setPaymentCode("DP" + String.format("%06d", payment.getPaymentId()));
        } else {
            payment.setPaymentCode("BL" + String.format("%06d", payment.getPaymentId()));
        }
        
        paymentRepository.save(payment);

        return mapToDto(payment);
    }

    @Override
    public GetPaymentResponse getPaymentById(int paymentId) {
        User user = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(user.getUserId());
        boolean isManager = user.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        
        Payment payment;
        if (isManager) {
            // Manager: chỉ check store
            payment = paymentRepository.findPaymentByPaymentIdAndOrder_Store(paymentId, store);
        } else {
            // Staff: check cả store và userId
            payment = paymentRepository.findByOrder_Store_StoreIdAndOrder_User_UserIdAndPaymentId(
                    store.getStoreId(), user.getUserId(), paymentId);
        }
        
        if (payment == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);
        }
        return mapToDto(payment);
    }

    @Override
    public Payment getPaymentEntityById(int paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
    }

    @Override
    public Payment getPaymentEntityByPaymentCode(String paymentCode) {
        return paymentRepository.findPaymentByPaymentCode(paymentCode);
    }

    @Override
    public List<GetPaymentResponse> getAllPaymentsByStore() {
        User user = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(user.getUserId());
        boolean isManager = user.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        
        List<Payment> payments;
        if (isManager) {
            // Manager: xem tất cả payments trong store
            payments = paymentRepository.findByOrder_Store(store);
        } else {
            // Staff: chỉ xem payments của chính họ trong store
            payments = paymentRepository.findByOrder_Store_StoreIdAndOrder_User_UserId(
                    store.getStoreId(), user.getUserId());
        }
        
        return payments.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void updatePaymentStatus(Payment payment, BigDecimal amount, PaymentStatus status) {
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentsByOrderAndPaymentType(int orderId, PaymentType paymentType) {
        Order order = orderService.getOrderEntityById(orderId);
        return paymentRepository.findByOrderAndPaymentType(order, paymentType);
    }

    private GetPaymentResponse mapToDto(Payment payment) {
        return GetPaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentCode(payment.getPaymentCode())
                .status(payment.getStatus())
                .paymentType(payment.getPaymentType())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .orderCode(payment.getOrder().getOrderCode())
                .build();
    }
}
