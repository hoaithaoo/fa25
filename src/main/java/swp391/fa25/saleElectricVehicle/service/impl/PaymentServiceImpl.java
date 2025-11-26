package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreateTransactionRequest;
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

    @Autowired
    @Lazy
    private TransactionService transactionService;

    @Autowired
    @Lazy
    private ContractService contractService;

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

        // Tính số tiền tự động dựa trên loại payment
        BigDecimal amount;
        if (request.getPaymentType() == PaymentType.DEPOSIT) {
            // Deposit: 20% của totalPayment
            BigDecimal DEPOSIT_PERCENTAGE = BigDecimal.valueOf(0.2);
            amount = order.getTotalPayment().multiply(DEPOSIT_PERCENTAGE);
        } else {
            // Balance: số tiền còn lại (totalPayment - paidAmount)
            BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
            amount = order.getTotalPayment().subtract(paidAmount);
        }

        // Tạo payment
        Payment payment = Payment.builder()
                .status(PaymentStatus.DRAFT)
                .paymentType(request.getPaymentType())
                .paymentMethod(request.getPaymentMethod())
                .amount(amount)
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

    @Override
    @org.springframework.transaction.annotation.Transactional
    public GetPaymentResponse confirmCashPayment(int paymentId) {
        // Lấy payment entity
        Payment payment = getPaymentEntityById(paymentId);
        
        // Validate payment method phải là CASH
        if (payment.getPaymentMethod() != PaymentMethod.CASH) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED, 
                "Chỉ có thể xác nhận thanh toán tiền mặt cho payment có phương thức CASH");
        }
        
        // Validate payment status phải là DRAFT
        if (payment.getStatus() != PaymentStatus.DRAFT) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED, 
                "Chỉ có thể xác nhận thanh toán cho payment ở trạng thái DRAFT");
        }
        
        // Lấy số tiền tự động từ payment (đã được set dựa trên payment type khi tạo)
        BigDecimal amount = payment.getAmount();
        
        // Tạo transaction để lưu thông tin thanh toán
        String transactionRef = "CASH_" + payment.getPaymentCode() + "_" + System.currentTimeMillis();
        CreateTransactionRequest transactionRequest = CreateTransactionRequest.builder()
                .paymentCode(payment.getPaymentCode())
                .transactionRef(transactionRef)
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .bankTransactionCode(null) // Cash không có bank transaction code
                .gateway(PaymentGateway.CASH)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        transactionService.createTransaction(transactionRequest);
        
        // Cập nhật payment status thành COMPLETED
        updatePaymentStatus(payment, amount, PaymentStatus.COMPLETED);
        
        // Cập nhật order paidAmount
        Order order = payment.getOrder();
        order.setPaidAmount(order.getPaidAmount().add(amount));
        orderService.updateOrder(order);
        
        // Nếu là payment deposit, update order status thành DEPOSIT_PAID
        if (payment.getPaymentType() == PaymentType.DEPOSIT) {
            orderService.updateOrderStatus(order, OrderStatus.DEPOSIT_PAID);
            
            // Nếu đã có contract đặt cọc, update contract status thành DEPOSIT_PAID
            if (contractService.hasDepositContract(order.getOrderId())) {
                Contract depositContract = contractService.getDepositContractByOrderId(order.getOrderId());
                contractService.updateContractStatus(depositContract, ContractStatus.DEPOSIT_PAID);
            }
        } else if (payment.getPaymentType() == PaymentType.BALANCE) {
            // Nếu là payment balance, tìm contract mua bán và update status thành FULLY_PAID
            if (contractService.hasSaleContract(order.getOrderId())) {
                Contract saleContract = contractService.getSaleContractByOrderId(order.getOrderId());
                contractService.updateContractStatus(saleContract, ContractStatus.FULLY_PAID);
            }
            
            // Kiểm tra xem đã thanh toán đủ chưa (paidAmount >= totalPayment)
            if (order.getPaidAmount().compareTo(order.getTotalPayment()) >= 0) {
                // Update order status thành FULLY_PAID
                orderService.updateOrderStatus(order, OrderStatus.FULLY_PAID);
            }
        }
        
        return mapToDto(payment);
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
