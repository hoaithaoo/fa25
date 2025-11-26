package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderWithItemsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.order.VehicleAssignment;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetQuoteResponse;

import java.util.List;

public interface OrderDetailService {
    //    // =============== CRUD OPERATIONS ===============
    GetQuoteResponse createQuote(CreateOrderWithItemsRequest request);
    GetQuoteResponse updateQuote(CreateOrderWithItemsRequest request);
//    // =============== BUSINESS OPERATIONS ===============
    List<GetOrderDetailsResponse> getOrderDetailsByOrderId(int orderId);
    List<GetOrderDetailsResponse> assignVehiclesToOrderDetails(List<VehicleAssignment> assignments);
}