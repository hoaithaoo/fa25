package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
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
                .storeId(order.getUser().getStore().getStoreId())
                .storeName(order.getUser().getStore().getStoreName())
                .storeAddress(order.getUser().getStore().getAddress())
                .build();
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
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public GetOrderResponse confirmOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        order.setStatus(OrderStatus.CONFIRMED);
        updateOrder(order);
        return mapToDto(order);
    }

    // không xóa được đơn hàng đã hoàn thành hoặc đã giao
    @Override
    public void deleteOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() == OrderStatus.COMPLETED
                || order.getStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE);
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
                .storeId(order.getUser().getStore() != null ? order.getUser().getStore().getStoreId() : 0)
                .storeName(order.getUser().getStore() != null ? order.getUser().getStore().getStoreName() : null)
                .orderDate(order.getOrderDate())
                .build();
    }
}