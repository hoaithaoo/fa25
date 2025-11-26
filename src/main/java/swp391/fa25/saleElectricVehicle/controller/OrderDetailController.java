package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderWithItemsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.order.VehicleAssignment;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.service.OrderDetailService;
import swp391.fa25.saleElectricVehicle.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private VehicleService vehicleService;

    // API 2.3: VALIDATE STOCK (KHÔNG LƯU DB)
//    @PostMapping("/validate")
//    public ResponseEntity<ApiResponse<StockValidationResponse>> validateStock(
//            @Valid @RequestBody StockValidationRequest request) {
//
//        StockValidationResponse validation =
//                orderDetailService.validateStockAvailability(request);
//        ApiResponse<StockValidationResponse> response = ApiResponse.<StockValidationResponse>builder()
//                .code(HttpStatus.CREATED.value())
//                .message("Order detail added successfully")
//                .data(validation)
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    // CREATE
//    @PostMapping("/create")
//    public ResponseEntity<ApiResponse<CreateOrderWithItemsResponse>> createOrderDetail(@RequestBody CreateOrderWithItemsRequest request) {
//        CreateOrderWithItemsResponse created = orderDetailService.createOrderDetail(request);
//        ApiResponse<CreateOrderWithItemsResponse> response = ApiResponse.<CreateOrderWithItemsResponse>builder()
//                .code(HttpStatus.CREATED.value())
//                .message("Order detail created successfully")
//                .data(created)
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

//    // READ - Get by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<GetOrderDetailsResponse>> getOrderDetailById(@PathVariable int id) {
//        GetOrderDetailsResponse orderDetail = orderDetailService.getOrderDetailById(id);
//        ApiResponse<GetOrderDetailsResponse> response = ApiResponse.<GetOrderDetailsResponse>builder()
//                .code(HttpStatus.OK.value())
//                .message("Order detail retrieved successfully")
//                .data(orderDetail)
//                .build();
//        return ResponseEntity.ok(response);
//    }

//
//    // UPDATE
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<OrderDetailDto>> updateOrderDetail(
//            @PathVariable int id,
//            @RequestBody OrderDetailDto orderDetailDto) {
//
//        OrderDetailDto updated = orderDetailService.updateOrderDetail(id, orderDetailDto);
//        ApiResponse<OrderDetailDto> response = ApiResponse.<OrderDetailDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Order detail updated successfully")
//                .data(updated)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    // DELETE
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> deleteOrderDetail(@PathVariable int id) {
//        orderDetailService.deleteOrderDetail(id);
//        ApiResponse<Void> response = ApiResponse.<Void>builder()
//                .code(HttpStatus.OK.value())
//                .message("Order detail deleted successfully")
//                .data(null)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    // BUSINESS - Update quantity
//    @PutMapping("/{id}/quantity/{quantity}")
//    public ResponseEntity<ApiResponse<OrderDetailDto>> updateQuantity(
//            @PathVariable int id,
//            @PathVariable int quantity) {
//
//        OrderDetailDto updated = orderDetailService.updateQuantity(id, quantity);
//        ApiResponse<OrderDetailDto> response = ApiResponse.<OrderDetailDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Quantity updated successfully")
//                .data(updated)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    // UPDATE QUOTE - Sửa báo giá cho order DRAFT
    @PutMapping("/quote/{orderId}")
    public ResponseEntity<ApiResponse<swp391.fa25.saleElectricVehicle.payload.response.order.GetQuoteResponse>> updateQuote(
            @PathVariable int orderId,
            @RequestBody CreateOrderWithItemsRequest request) {
        
        // Set orderId từ path variable
        request.setOrderId(orderId);
        
        swp391.fa25.saleElectricVehicle.payload.response.order.GetQuoteResponse updatedQuote = orderDetailService.updateQuote(request);
        ApiResponse<swp391.fa25.saleElectricVehicle.payload.response.order.GetQuoteResponse> response = ApiResponse.<swp391.fa25.saleElectricVehicle.payload.response.order.GetQuoteResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Báo giá đã được cập nhật thành công")
                .data(updatedQuote)
                .build();
        return ResponseEntity.ok(response);
    }

    // ASSIGN VEHICLES TO ORDER DETAILS - Gán xe cho order details (có thể 1 hoặc nhiều)
    @PutMapping("/assign-vehicles")
    public ResponseEntity<ApiResponse<List<GetOrderDetailsResponse>>> assignVehiclesToOrderDetails(
            @RequestBody List<VehicleAssignment> assignments) {
        List<GetOrderDetailsResponse> updatedOrderDetails = vehicleService.assignVehiclesToOrderDetails(assignments);
        ApiResponse<List<GetOrderDetailsResponse>> response = ApiResponse.<List<GetOrderDetailsResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Gán xe cho đơn hàng thành công")
                .data(updatedOrderDetails)
                .build();
        return ResponseEntity.ok(response);
    }

}