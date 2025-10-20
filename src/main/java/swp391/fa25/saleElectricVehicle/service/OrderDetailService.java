package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderDetailsRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderDetailsResponse;

import java.math.BigDecimal;
import java.util.List;

public interface OrderDetailService {

//    // =============== CRUD OPERATIONS ===============
//    CreateOrderDetailsResponse createOrderDetail(CreateOrderDetailsRequest request);
//    OrderDetailDto getOrderDetailById(int id);
//    List<OrderDetailDto> getAllOrderDetails();
//    OrderDetailDto updateOrderDetail(int id, OrderDetailDto orderDetailDto);
//    void deleteOrderDetail(int id);
//
//    // =============== BUSINESS OPERATIONS ===============
//    List<OrderDetailDto> getOrderDetailsByOrderId(int orderId);
//    OrderDetailDto updateQuantity(int id, int quantity);
//
//    // =============== VALIDATION ===============
////    boolean validateStockAvailability(int storeStockId, int requestedQuantity);
//
//    // =============== CALCULATION ===============
//    BigDecimal calculateTotalPrice(BigDecimal unitPrice, int quantity,
//                                   BigDecimal vatAmount, BigDecimal licensePlateFee,
//                                   BigDecimal registrationFee, BigDecimal discountAmount);
}