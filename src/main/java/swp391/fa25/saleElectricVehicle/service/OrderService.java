package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    // CRUD operations
    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderById(int orderId);
    List<OrderDto> getAllOrders();
    OrderDto updateOrder(int orderId, OrderDto orderDto);
    void deleteOrder(int orderId);

    // Business operations
    OrderDto updateOrderStatus(int orderId, Order.OrderStatus status);
    List<OrderDto> getOrdersByCustomerId(int customerId);
    List<OrderDto> getOrdersByStaffId(int staffId);
    List<OrderDto> getOrdersByStatus(Order.OrderStatus status);
    List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<OrderDto> searchOrdersByCustomerPhone(String phone);

    // Analytics
    long countOrdersByStatus(Order.OrderStatus status);
    List<OrderDto> getRecentOrders();
}