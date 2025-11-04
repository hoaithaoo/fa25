package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderDetailsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderWithItemsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.stock.StockValidationRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderWithItemsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetQuoteResponse;
import swp391.fa25.saleElectricVehicle.payload.response.stock.StockValidationResponse;

import java.math.BigDecimal;
import java.util.List;

public interface OrderDetailService {
//    StockValidationResponse validateStockAvailability(StockValidationRequest request);

//    // =============== CRUD OPERATIONS ===============
    GetQuoteResponse createQuote(CreateOrderWithItemsRequest request);
//    CreateOrderWithItemsResponse createOrderDetail(CreateOrderWithItemsRequest request);
    GetOrderDetailsResponse getOrderDetailById(int id);
//    OrderDetailDto getOrderDetailById(int id);
//    List<OrderDetailDto> getAllOrderDetails();
//    OrderDetailDto updateOrderDetail(int id, OrderDetailDto orderDetailDto);
//    void deleteOrderDetail(int id);
//
//    // =============== BUSINESS OPERATIONS ===============
    List<GetOrderDetailsResponse> getOrderDetailsByOrderId(int orderId);
//    OrderDetailDto updateQuantity(int id, int quantity);
//
//    // =============== VALIDATION ===============
//    boolean validateStockAvailability(int storeStockId, int requestedQuantity);
//
//    // =============== CALCULATION ===============
//    BigDecimal calculateTotalPrice(BigDecimal unitPrice, int quantity,
//                                   BigDecimal vatAmount, BigDecimal licensePlateFee,
//                                   BigDecimal registrationFee, BigDecimal discountAmount);
}