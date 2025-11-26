package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.service.*;
import swp391.fa25.saleElectricVehicle.service.DepositAgreementService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DepositAgreementServiceImpl implements DepositAgreementService {

    private final BigDecimal DEPOSIT_PERCENTAGE = BigDecimal.valueOf(0.2); // 20% deposit

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void createDepositRequest(int orderId) {
        // Validate order, phải ở trạng thái CONFIRMED
        Order order = orderService.getOrderEntityById(orderId);
        if (!order.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new AppException(ErrorCode.ORDER_NOT_IN_CONFIRMED_STATUS, 
                "Chỉ có thể tạo phiếu thỏa thuận đặt cọc cho đơn hàng đã được xác nhận");
        }

        // Set payment deadline: 4 giờ từ bây giờ
        LocalDateTime paymentDeadline = LocalDateTime.now().plusHours(4);
        
        // Update order status và payment deadline
        orderService.updateOrderStatusWithDeadline(order, OrderStatus.PENDING_DEPOSIT, paymentDeadline);
    }

    @Override
    public String generateDepositAgreementHtml(int orderId) {
        // Lấy thông tin order
        OrderDto order = orderService.getOrderDtoById(orderId);
        CustomerDto customer = customerService.getCustomerById(order.getCustomerId());
        StoreDto store = storeService.getStoreById(order.getStoreId());

        // Tính tiền đặt cọc: 20% của tổng tiền
        BigDecimal depositAmount = order.getTotalUnitPrice().multiply(DEPOSIT_PERCENTAGE);
        LocalDateTime paymentDeadline = order.getPaymentDeadline();

        // Chuẩn bị model data cho template engine
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("customer", customer);
        context.setVariable("store", store);
        context.setVariable("depositAmount", depositAmount);
        context.setVariable("paymentDeadline", paymentDeadline);
        context.setVariable("agreementDate", LocalDateTime.now());

        // Render ra HTML dựa trên template deposit-agreement.html
        return templateEngine.process("deposit-agreement", context);
    }
}

