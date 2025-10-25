package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;
import swp391.fa25.saleElectricVehicle.repository.OrderRepository;
import swp391.fa25.saleElectricVehicle.service.CustomerService;
import swp391.fa25.saleElectricVehicle.service.OrderService;
import swp391.fa25.saleElectricVehicle.service.StoreService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerService.getCustomerEntityById(request.getCustomerId());
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        Order savedOrder = orderRepository.save(Order.builder()
                .totalPrice(BigDecimal.ZERO)
                .totalTaxPrice(BigDecimal.ZERO)
                .totalPromotionAmount(BigDecimal.ZERO)
                .totalPayment(BigDecimal.ZERO)
                .status(OrderStatus.DRAFT) // Default status
                .orderDate(LocalDateTime.now())
                .customer(customer)
                .user(staff)
                .build());
        String orderCode = "ORD-" + String.format("%06d", savedOrder.getOrderId());
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
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        return mapToDto(order);
    }

    @Override
    public List<GetOrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
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
    public void updateAfterDetailChange(Order order) {
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        orderRepository.delete(order);
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
        return orderRepository.findByCustomer_CustomerId(customerId)
                .stream()
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
    public List<GetOrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(OrderStatus.valueOf(status.toUpperCase())).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<GetOrderResponse> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime for the full day range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return orderRepository.findByOrderDateBetween(startDateTime, endDateTime).stream()
                .map(this::mapToDto)
                .toList();
    }

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
                                .vatAmount(od.getVatAmount())
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
                .storeId(order.getUser().getStore() != null ? order.getUser().getStore().getStoreId() : 0)
                .storeName(order.getUser().getStore() != null ? order.getUser().getStore().getStoreName() : null)
                .orderDate(order.getOrderDate())
                .build();
    }
}