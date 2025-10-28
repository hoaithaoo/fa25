package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderWithItemsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.stock.StockValidationRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderWithItemsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.stock.StockValidationResponse;
import swp391.fa25.saleElectricVehicle.service.OrderDetailService;

import java.util.List;

@RestController
@RequestMapping("/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    // API 2.3: VALIDATE STOCK (KHÔNG LƯU DB)
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<StockValidationResponse>> validateStock(
            @Valid @RequestBody StockValidationRequest request) {

        StockValidationResponse validation =
                orderDetailService.validateStockAvailability(request);
        ApiResponse<StockValidationResponse> response = ApiResponse.<StockValidationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Order detail added successfully")
                .data(validation)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateOrderWithItemsResponse>> createOrderDetail(@RequestBody CreateOrderWithItemsRequest request) {
        CreateOrderWithItemsResponse created = orderDetailService.createOrderDetail(request);
        ApiResponse<CreateOrderWithItemsResponse> response = ApiResponse.<CreateOrderWithItemsResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Order detail created successfully")
                .data(created)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetOrderDetailsResponse>> getOrderDetailById(@PathVariable int id) {
        GetOrderDetailsResponse orderDetail = orderDetailService.getOrderDetailById(id);
        ApiResponse<GetOrderDetailsResponse> response = ApiResponse.<GetOrderDetailsResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Order detail retrieved successfully")
                .data(orderDetail)
                .build();
        return ResponseEntity.ok(response);
    }

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

}