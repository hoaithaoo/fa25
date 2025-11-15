package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailsDto;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;
import swp391.fa25.saleElectricVehicle.repository.OrderRepository;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    UserService userService;

    @Autowired
    StoreService storeService;

    @Autowired
    StoreStockService storeStockService;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerService.getCustomerEntityById(request.getCustomerId());
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // Validation: Đảm bảo staff có store
        if (staff.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST, "Staff phải thuộc một store");
        }

        // Tạo order - store sẽ được tự động set từ user.store bởi @PrePersist
        Order newOrder = Order.builder()
                .totalPrice(BigDecimal.ZERO)
                .totalTaxPrice(BigDecimal.ZERO)
                .totalPromotionAmount(BigDecimal.ZERO)
                .totalPayment(BigDecimal.ZERO)
                .status(OrderStatus.DRAFT) // Default status
                .orderDate(LocalDateTime.now())
                .customer(customer)
                .user(staff)
                .build();
        
        // @PrePersist sẽ tự động set store = staff.getStore()
        Order savedOrder = orderRepository.save(newOrder);
        String orderCode = "ORD" + String.format("%06d", savedOrder.getOrderId());
        savedOrder.setOrderCode(orderCode);
        savedOrder = orderRepository.save(savedOrder);

        return CreateOrderResponse.builder()
                .orderId(savedOrder.getOrderId())
                .orderCode(orderCode)
                .totalPrice(savedOrder.getTotalPrice())
                .totalTaxPrice(savedOrder.getTotalTaxPrice())
                .totalPromotionAmount(savedOrder.getTotalPromotionAmount())
                .totalPayment(savedOrder.getTotalPayment())
                .customerId(customer.getCustomerId())
                .customerName(customer.getFullName())
                .customerPhone(customer.getPhone())
                .staffId(staff.getUserId())
                .staffName(staff.getFullName())
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .orderDate(savedOrder.getOrderDate())
                .build();
    }

    @Override
    public GetOrderResponse getOrderById(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        Order order = orderRepository.findByStore_StoreIdAndOrderId(store.getStoreId(), orderId);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        return mapToDto(order);
    }

    @Override
    public OrderDto getOrderDtoById(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        List<OrderDetail> list = order.getOrderDetails();
        int count = 0;
        BigDecimal totalUnitPrice = BigDecimal.ZERO;
        BigDecimal totalTaxPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (OrderDetail detail : list) {
            count += detail.getQuantity(); // đếm tổng số lượng xe trong đơn hàng
            totalUnitPrice = totalUnitPrice.add(detail.getUnitPrice());
            totalTaxPrice = totalTaxPrice.add(detail.getLicensePlateFee().add(detail.getRegistrationFee()));
            totalDiscount = totalDiscount.add(detail.getDiscountAmount());
        }

        return OrderDto.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderDetailsList(order.getOrderDetails().stream()
                        .map(od -> OrderDetailsDto.builder()
                                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                                .modelYear(od.getStoreStock().getModelColor().getModel().getModelYear())
                                .seatingCapacity(od.getStoreStock().getModelColor().getModel().getSeatingCapacity())
                                .bodyType(od.getStoreStock().getModelColor().getModel().getBodyType().name())
                                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                                .quantity(od.getQuantity())
                                .unitPrice(od.getUnitPrice())
                                .discount(od.getDiscountAmount())
                                .totalTax(od.getLicensePlateFee().add(od.getRegistrationFee())) // biển số và đăng ký
                                .totalPrice(od.getTotalPrice())
                                .build())
                        .toList())
                .totalQuantity(count)
                .totalUnitPrice(totalUnitPrice)
                .totalTaxPrice(totalTaxPrice)
                .totalDiscount(totalDiscount)
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getFullName())
                .staffId(order.getUser().getUserId())
                .staffName(order.getUser().getFullName())
                .storeId(order.getStore().getStoreId())
                .storeName(order.getStore().getStoreName())
                .storeAddress(order.getStore().getAddress())
                .build();
    }

    @Override
    public List<GetOrderResponse> getAllOrdersByStore() {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        List<Order> order = orderRepository.findByStore_StoreId(store.getStoreId());
        return order.stream()
                .map(this::mapToDto)
                .toList();
    }

//    @Override
//    public OrderDto updateOrder(int orderId, OrderDto orderDto) {
//        Order order = orderRepository.findById(orderId).orElse(null);
//        if (order == null) {
//            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
//        }
//
//        // Update fields
//        if (orderDto.getTotalPrice() != null && orderDto.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
//            order.setTotalPrice(orderDto.getTotalPrice());
//        }
//        if (orderDto.getTotalTaxPrice() != null && orderDto.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
//            order.setTotalTaxPrice(orderDto.getTotalTaxPrice());
//        }
//        if (orderDto.getTotalPromotionAmount() != null && orderDto.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
//            order.setTotalPromotionAmount(orderDto.getTotalPromotionAmount());
//        }
//        if (orderDto.getTotalPayment() != null && orderDto.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
//            order.setTotalPayment(orderDto.getTotalPayment());
//        }
//        if (orderDto.getStatus() != null) {
//            order.setStatus(orderDto.getStatus());
//        }
//
//        order.setUpdatedAt(LocalDateTime.now());
//        Order savedOrder = orderRepository.save(order);
//        return mapToDto(savedOrder);
//    }

    @Override
    @Transactional
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Order order, OrderStatus status) {
        // ✅ Khi order được DELIVERED, trừ stock thực tế và unlock reserved
        if (status == OrderStatus.DELIVERED && order.getStatus() != OrderStatus.DELIVERED) {
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                StoreStock stock = orderDetail.getStoreStock();
                
                // Trừ số lượng thực tế
                stock.setQuantity(stock.getQuantity() - orderDetail.getQuantity());
                
                // Unlock reserved quantity
                stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - orderDetail.getQuantity()));
                
                storeStockService.updateStoreStock(stock);
            }
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public GetOrderResponse confirmOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        // không thể xác nhận đơn hàng nếu không có sản phẩm nào
        if (order.getOrderDetails().isEmpty()) {
            throw new AppException(ErrorCode.ORDER_NO_ITEMS);
        }

        // ✅ Reserve stock cho tất cả order details
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            StoreStock stock = orderDetail.getStoreStock();
            int availableStock = stock.getQuantity() - stock.getReservedQuantity();

            if (availableStock < orderDetail.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK,
                    String.format("Sản phẩm %s không đủ hàng để xác nhận đơn. Còn %d, yêu cầu %d",
                        stock.getModelColor().getModel().getModelName(),
                        availableStock,
                        orderDetail.getQuantity()));
            }

            // Reserve stock
            stock.setReservedQuantity(stock.getReservedQuantity() + orderDetail.getQuantity());
            storeStockService.updateStoreStock(stock);
        }

        updateOrderStatus(order, OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian để track timeout
        orderRepository.save(order);
        return mapToDto(order);
    }

    // không xóa được đơn hàng đã ký hợp đồng, đã thanh toán đặt cọc, đã thanh toán đầy đủ, đã giao hàng
    @Override
    @Transactional
    public void deleteOrder(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        Order order = orderRepository.findByStore_StoreIdAndOrderId(store.getStoreId(), orderId);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() == OrderStatus.CONTRACT_SIGNED
                || order.getStatus() == OrderStatus.DEPOSIT_PAID
                || order.getStatus() == OrderStatus.FULLY_PAID
                || order.getStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE);
        }

        // ✅ Unlock stock nếu order đã được CONFIRMED
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            unlockStockForOrder(order);
        }

        orderRepository.delete(order);
    }

    // ✅ Method để unlock stock khi order bị hủy
    private void unlockStockForOrder(Order order) {
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            StoreStock stock = orderDetail.getStoreStock();
            int currentReserved = stock.getReservedQuantity();
            int unlockAmount = orderDetail.getQuantity();

            // Đảm bảo không unlock quá số đã reserve
            stock.setReservedQuantity(Math.max(0, currentReserved - unlockAmount));
            storeStockService.updateStoreStock(stock);
        }
    }

//    @Override
//    public OrderDto updateOrderStatus(int orderId, OrderStatus status) {
//        Order order = orderRepository.findById(orderId).orElse(null);
//        if (order == null) {
//            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
//        }
//
//        order.setStatus(status);
//        order.setUpdatedAt(LocalDateTime.now());
//        Order savedOrder = orderRepository.save(order);
//        return mapToDto(savedOrder);
//    }
//
    @Override
    public List<GetOrderResponse> getOrdersByCustomerId(int customerId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        List<Order> orders = orderRepository.findByCustomer_CustomerIdAndStore_StoreId(customerId, store.getStoreId());
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<GetOrderResponse> getOrdersByStaffId(int staffId) {
        return orderRepository.findByUser_UserId(staffId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<GetOrderResponse> getOrdersByCurrentStaff() {
        User currentUser = userService.getCurrentUserEntity();
        return orderRepository.findByUser_UserId(currentUser.getUserId()).stream()
                .map(this::mapToDto)
                .toList();
    }

//    @Override
//    public List<GetOrderResponse> getOrdersByStatus(OrderStatus status) {
//        return orderRepository.findByStatus(OrderStatus.valueOf(status.toUpperCase())).stream()
//                .map(this::mapToDto)
//                .toList();
//    }
//
//    @Override
//    public List<GetOrderResponse> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
//        // Convert LocalDate to LocalDateTime for the full day range
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
//        return orderRepository.findByOrderDateBetween(startDateTime, endDateTime).stream()
//                .map(this::mapToDto)
//                .toList();
//    }

//    @Override
//    public List<OrderDto> searchOrdersByCustomerPhone(String phone) {
//        return orderRepository.findByCustomerPhone(phone).stream()
//                .map(this::mapToDto)
//                .toList();
//    }
//
//    @Override
//    public long countOrdersByStatus(OrderStatus status) {
//
//        return orderRepository.countByStatus(status);
//    }
//
//    @Override
//    public List<OrderDto> getRecentOrders() {
//        return orderRepository.findTop10ByOrderByOrderDateDesc()
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//    }

    @Override
    public Order getOrderEntityById(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        return order;
    }

    // Helper method to convert Entity to DTO
    private GetOrderResponse mapToDto(Order order) {
        return GetOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .getOrderDetailsResponses(order.getOrderDetails().stream()
                        .map(od -> GetOrderDetailsResponse.builder()
                                .orderDetailId(od.getId())
                                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                                .unitPrice(od.getUnitPrice())
                                .quantity(od.getQuantity())
//                                .vatAmount(od.getVatAmount())
                                .licensePlateFee(od.getLicensePlateFee())
                                .registrationFee(od.getRegistrationFee())
                                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : null)
                                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                                .discountAmount(od.getDiscountAmount())
                                .totalPrice(od.getTotalPrice())
                                .build())
                        .toList())
                .totalPrice(order.getTotalPrice())
                .totalTaxPrice(order.getTotalTaxPrice())
                .totalPromotionAmount(order.getTotalPromotionAmount())
                .totalPayment(order.getTotalPayment())
                .status(order.getStatus().name())
                .contractId(order.getContract() != null ? order.getContract().getContractId() : 0)
                .contractCode(order.getContract() != null ? order.getContract().getContractCode() : null)
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getFullName())
                .customerPhone(order.getCustomer().getPhone())
                .feedbackId(order.getFeedback() != null ? order.getFeedback().getFeedbackId() : 0)
                .staffId(order.getUser().getUserId())
                .staffName(order.getUser().getFullName())
                .storeId(order.getStore().getStoreId())
                .storeName(order.getStore().getStoreName())
                .orderDate(order.getOrderDate())
                .build();
    }

    // ✅ Auto-cancel DRAFT orders sau 24 giờ
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    @Transactional
    public void autoCancelExpiredDraftOrders() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(24); // 24 giờ

        List<Order> expiredOrders = orderRepository.findByStatusAndOrderDateBefore(
            OrderStatus.DRAFT,
            expiryTime
        );

        for (Order order : expiredOrders) {
            // DRAFT orders chưa reserve stock nên không cần unlock
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    // ✅ Auto-cancel CONFIRMED orders không thanh toán đặt cọc sau 48 giờ
    @Scheduled(fixedRate = 3600000) // Mỗi giờ
    @Transactional
    public void autoCancelUnpaidConfirmedOrders() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(48); // 48 giờ sau khi CONFIRMED

        List<Order> unpaidOrders = orderRepository.findByStatusAndUpdatedAtBefore(
            OrderStatus.CONFIRMED,
            expiryTime
        );

        for (Order order : unpaidOrders) {
            // Kiểm tra xem đã có payment deposit chưa
            boolean hasDepositPayment = false;
            if (order.getContract() != null) {
                // Kiểm tra contract status
                if (order.getContract().getStatus() == ContractStatus.DEPOSIT_PAID
                        || order.getContract().getStatus() == ContractStatus.FULLY_PAID
                        || order.getContract().getStatus() == ContractStatus.COMPLETED) {
                    hasDepositPayment = true;
                }
            }

            // Nếu chưa có deposit payment thì hủy và unlock stock
            if (!hasDepositPayment) {
                unlockStockForOrder(order);
                order.setStatus(OrderStatus.CANCELLED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            }
        }
    }
}