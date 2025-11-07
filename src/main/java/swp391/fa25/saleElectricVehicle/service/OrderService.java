package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    // CRUD operations
    CreateOrderResponse createOrder(CreateOrderRequest request);
    GetOrderResponse getOrderById(int orderId);
    OrderDto getOrderDtoById(int orderId);
    List<GetOrderResponse> getAllOrdersByStore();
//    GetOrderResponse updateOrder(int orderId, OrderDto orderDto);
    void updateOrder(Order order);
    void updateOrderStatus(Order order, OrderStatus status);
    GetOrderResponse confirmOrder(int orderId);
    void deleteOrder(int orderId);
    Order getOrderEntityById(int orderId);

    // Business operations
//    OrderDto updateOrderStatus(int orderId, OrderStatus status);
    List<GetOrderResponse> getOrdersByCustomerId(int customerId);
    List<GetOrderResponse> getOrdersByStaffId(int staffId);
    List<GetOrderResponse> getOrdersByCurrentStaff();
//    List<GetOrderResponse> getOrdersByStatus(OrderStatus status);
//    List<GetOrderResponse> getOrdersByDateRange(LocalDate startDate, LocalDate endDate);
//    List<OrderDto> searchOrdersByCustomerPhone(String phone);
//
//    // Analytics
//    long countOrdersByStatus(OrderStatus status);
//    List<OrderDto> getRecentOrders();
}