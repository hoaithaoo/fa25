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
    //    // =============== CRUD OPERATIONS ===============
    GetQuoteResponse createQuote(CreateOrderWithItemsRequest request);
    GetQuoteResponse updateQuote(CreateOrderWithItemsRequest request);
//    // =============== BUSINESS OPERATIONS ===============
    List<GetOrderDetailsResponse> getOrderDetailsByOrderId(int orderId);
}