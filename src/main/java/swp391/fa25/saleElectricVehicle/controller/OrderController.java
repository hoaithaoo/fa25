package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Order created successfully")
                .data(createdOrder)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ - Get by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable int orderId) {
        OrderDto orderDto = orderService.getOrderById(orderId);
        ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                .code(HttpStatus.OK.value())
                .message("Order retrieved successfully")
                .data(orderDto)
                .build();
        return ResponseEntity.ok(response);
    }

    // READ - Get all
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/update/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrder(
            @PathVariable int orderId,
            @RequestBody OrderDto orderDto) {

        OrderDto updatedOrder = orderService.updateOrder(orderId, orderDto);
        ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                .code(HttpStatus.OK.value())
                .message("Order updated successfully")
                .data(updatedOrder)
                .build();
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable int orderId) {
        orderService.deleteOrder(orderId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Order deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Update order status
    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable int orderId,
            @PathVariable String status) {

        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, orderStatus);

        ApiResponse<OrderDto> response = ApiResponse.<OrderDto>builder()
                .code(HttpStatus.OK.value())
                .message("Order status updated successfully")
                .data(updatedOrder)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Get orders by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByCustomer(@PathVariable int customerId) {
        List<OrderDto> orders = orderService.getOrdersByCustomerId(customerId);
        ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Customer orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Get orders by staff
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByStaff(@PathVariable int staffId) {
        List<OrderDto> orders = orderService.getOrdersByStaffId(staffId);
        ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Staff orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByStatus(@PathVariable String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        List<OrderDto> orders = orderService.getOrdersByStatus(orderStatus);
        ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Orders by status retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Search by customer phone
    @GetMapping("/search/phone/{phone}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> searchOrdersByPhone(@PathVariable String phone) {
        List<OrderDto> orders = orderService.searchOrdersByCustomerPhone(phone);
        ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Orders found by phone successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Get orders by date range
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<OrderDto> orders = orderService.getOrdersByDateRange(startDate, endDate);
        ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Orders by date range retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    // ANALYTICS - Get recent orders
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getRecentOrders() {
        List<OrderDto> orders = orderService.getRecentOrders();
        ApiResponse<List<OrderDto>> response = ApiResponse.<List<OrderDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Recent orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    // ANALYTICS - Count orders by status
    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countOrdersByStatus(@PathVariable String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        long count = orderService.countOrdersByStatus(orderStatus);
        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .code(HttpStatus.OK.value())
                .message("Order count retrieved successfully")
                .data(count)
                .build();
        return ResponseEntity.ok(response);
    }
}