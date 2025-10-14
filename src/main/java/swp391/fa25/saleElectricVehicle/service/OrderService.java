package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    // CRUD operations
    CreateOrderResponse createOrder(CreateOrderRequest request);
    GetOrderResponse getOrderById(int orderId);
    List<GetOrderResponse> getAllOrders();
//    OrderDto updateOrder(int orderId, OrderDto orderDto);
    void deleteOrder(int orderId);

    // Business operations
//    OrderDto updateOrderStatus(int orderId, OrderStatus status);
//    List<OrderDto> getOrdersByCustomerId(int customerId);
//    List<OrderDto> getOrdersByStaffId(int staffId);
//    List<OrderDto> getOrdersByStatus(OrderStatus status);
//    List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
//    List<OrderDto> searchOrdersByCustomerPhone(String phone);
//
//    // Analytics
//    long countOrdersByStatus(OrderStatus status);
//    List<OrderDto> getRecentOrders();
}