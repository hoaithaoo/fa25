package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailDto;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.OrderDetailService;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
@CrossOrigin(origins = "*")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderDetailDto>> createOrderDetail(@RequestBody OrderDetailDto orderDetailDto) {
        OrderDetailDto created = orderDetailService.createOrderDetail(orderDetailDto);
        ApiResponse<OrderDetailDto> response = ApiResponse.<OrderDetailDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Order detail created successfully")
                .data(created)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailDto>> getOrderDetailById(@PathVariable int id) {
        OrderDetailDto orderDetail = orderDetailService.getOrderDetailById(id);
        ApiResponse<OrderDetailDto> response = ApiResponse.<OrderDetailDto>builder()
                .code(HttpStatus.OK.value())
                .message("Order detail retrieved successfully")
                .data(orderDetail)
                .build();
        return ResponseEntity.ok(response);
    }

    // READ - Get by Order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<OrderDetailDto>>> getOrderDetailsByOrderId(@PathVariable int orderId) {
        List<OrderDetailDto> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        ApiResponse<List<OrderDetailDto>> response = ApiResponse.<List<OrderDetailDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Order details retrieved successfully")
                .data(orderDetails)
                .build();
        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailDto>> updateOrderDetail(
            @PathVariable int id,
            @RequestBody OrderDetailDto orderDetailDto) {

        OrderDetailDto updated = orderDetailService.updateOrderDetail(id, orderDetailDto);
        ApiResponse<OrderDetailDto> response = ApiResponse.<OrderDetailDto>builder()
                .code(HttpStatus.OK.value())
                .message("Order detail updated successfully")
                .data(updated)
                .build();
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrderDetail(@PathVariable int id) {
        orderDetailService.deleteOrderDetail(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Order detail deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    // BUSINESS - Update quantity
    @PutMapping("/{id}/quantity/{quantity}")
    public ResponseEntity<ApiResponse<OrderDetailDto>> updateQuantity(
            @PathVariable int id,
            @PathVariable int quantity) {

        OrderDetailDto updated = orderDetailService.updateQuantity(id, quantity);
        ApiResponse<OrderDetailDto> response = ApiResponse.<OrderDetailDto>builder()
                .code(HttpStatus.OK.value())
                .message("Quantity updated successfully")
                .data(updated)
                .build();
        return ResponseEntity.ok(response);
    }

}