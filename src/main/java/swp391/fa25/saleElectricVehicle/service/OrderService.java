package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;

import java.util.List;

public interface OrderService {
    // CRUD operations
    CreateOrderResponse createOrder(CreateOrderRequest request);
    GetOrderResponse getOrderById(int orderId);
    List<GetOrderResponse> getAllOrders();
//    GetOrderResponse updateOrder(int orderId, OrderDto orderDto);
    void updateAfterDetailChange(Order order);
    void deleteOrder(int orderId);
    Order getOrderEntityById(int orderId);

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