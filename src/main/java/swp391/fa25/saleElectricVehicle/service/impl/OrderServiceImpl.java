package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.repository.CustomerRepository;
import swp391.fa25.saleElectricVehicle.repository.OrderRepository;
import swp391.fa25.saleElectricVehicle.repository.UserRepository;
import swp391.fa25.saleElectricVehicle.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        // Validate customer exists
        Customer customer = customerRepository.findById(orderDto.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        // Validate staff exists
        User staff = userRepository.findById(orderDto.getStaffId()).orElse(null);
        if (staff == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        Order order = Order.builder()
                .totalPrice(orderDto.getTotalPrice())
                .totalTaxPrice(orderDto.getTotalTaxPrice())
                .totalPromotionAmount(orderDto.getTotalPromotionAmount())
                .totalPayment(orderDto.getTotalPayment())
                .status(orderDto.getStatus() != null ? orderDto.getStatus() : Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .customer(customer)
                .user(staff)
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }

    @Override
    public OrderDto getOrderById(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        return mapToDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public OrderDto updateOrder(int orderId, OrderDto orderDto) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        // Update fields
        if (orderDto.getTotalPrice() != null) {
            order.setTotalPrice(orderDto.getTotalPrice());
        }
        if (orderDto.getTotalTaxPrice() != null) {
            order.setTotalTaxPrice(orderDto.getTotalTaxPrice());
        }
        if (orderDto.getTotalPromotionAmount() != null) {
            order.setTotalPromotionAmount(orderDto.getTotalPromotionAmount());
        }
        if (orderDto.getTotalPayment() != null) {
            order.setTotalPayment(orderDto.getTotalPayment());
        }
        if (orderDto.getStatus() != null) {
            order.setStatus(orderDto.getStatus());
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }

    @Override
    public void deleteOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.delete(order);
    }

    @Override
    public OrderDto updateOrderStatus(int orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }

    @Override
    public List<OrderDto> getOrdersByCustomerId(int customerId) {
        return orderRepository.findByCustomer_CustomerId(customerId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersByStaffId(int staffId) {
        return orderRepository.findByUser_UserId(staffId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderDto> searchOrdersByCustomerPhone(String phone) {
        return orderRepository.findByCustomerPhone(phone).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public long countOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public List<OrderDto> getRecentOrders() {
        return orderRepository.findTop10ByOrderByOrderDateDesc().stream()
                .map(this::mapToDto)
                .toList();
    }

    // Helper method to convert Entity to DTO
    private OrderDto mapToDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .totalTaxPrice(order.getTotalTaxPrice())
                .totalPromotionAmount(order.getTotalPromotionAmount())
                .totalPayment(order.getTotalPayment())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .updatedAt(order.getUpdatedAt())
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getFullName())
                .customerPhone(order.getCustomer().getPhone())
                .staffId(order.getUser().getUserId())
                .staffName(order.getUser().getFullName())
                .statusDisplay(order.getStatus().name())
                .build();
    }
}