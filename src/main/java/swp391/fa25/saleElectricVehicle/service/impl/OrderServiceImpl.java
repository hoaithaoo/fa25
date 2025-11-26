package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailsDto;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.StaffMonthlyOrdersResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.StoreMonthlyRevenueResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.VehicleSimpleResponse;
import swp391.fa25.saleElectricVehicle.repository.OrderRepository;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import swp391.fa25.saleElectricVehicle.entity.Payment;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    UserService userService;

    @Autowired
    StoreService storeService;

    @Autowired
    StoreStockService storeStockService;

    @Autowired
    VehicleService vehicleService;

    @Autowired
    @Lazy
    OrderDetailService orderDetailService;

    @Autowired
    @Lazy
    PaymentService paymentService;

    @Autowired
    @Lazy
    ContractService contractService;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerService.getCustomerEntityById(request.getCustomerId());
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // Tạo order - store sẽ được tự động set từ user.store bởi @PrePersist
        Order newOrder = Order.builder()
                .totalPrice(BigDecimal.ZERO)
                .totalTaxPrice(BigDecimal.ZERO)
                .totalPromotionAmount(BigDecimal.ZERO)
                .totalPayment(BigDecimal.ZERO)
                .status(OrderStatus.DRAFT) // Default status
                .orderDate(LocalDateTime.now())
                .customer(customer)
                .user(staff)
                .build();
        
        // @PrePersist sẽ tự động set store = staff.getStore()
        Order savedOrder = orderRepository.save(newOrder);
        String orderCode = "ORD" + String.format("%06d", savedOrder.getOrderId());
        savedOrder.setOrderCode(orderCode);
        savedOrder = orderRepository.save(savedOrder);

        return CreateOrderResponse.builder()
                .orderId(savedOrder.getOrderId())
                .orderCode(orderCode)
                .totalPrice(savedOrder.getTotalPrice())
                .totalTaxPrice(savedOrder.getTotalTaxPrice())
                .totalPromotionAmount(savedOrder.getTotalPromotionAmount())
                .totalPayment(savedOrder.getTotalPayment())
                .customerId(customer.getCustomerId())
                .customerName(customer.getFullName())
                .customerPhone(customer.getPhone())
                .staffId(staff.getUserId())
                .staffName(staff.getFullName())
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .orderDate(savedOrder.getOrderDate())
                .build();
    }

    @Override
    public GetOrderResponse getOrderById(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        
        Order order;
        if (isManager) {
            // Manager: chỉ cần check store
            order = orderRepository.findByStore_StoreIdAndOrderId(store.getStoreId(), orderId);
        } else {
            // Staff: check cả store và userId
            order = orderRepository.findByStore_StoreIdAndUser_UserIdAndOrderId(
                    store.getStoreId(), currentUser.getUserId(), orderId);
        }
        
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        return mapToDto(order);
    }

    @Override
    public OrderDto getOrderDtoById(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        List<OrderDetail> list = order.getOrderDetails();
        // Đếm tổng số lượng xe: cộng quantity của tất cả detail
        int count = 0;
        BigDecimal totalUnitPrice = BigDecimal.ZERO;
        BigDecimal totalTaxPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (OrderDetail detail : list) {
            count += detail.getQuantity(); // cộng quantity của mỗi detail
            totalUnitPrice = totalUnitPrice.add(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            // Các loại phí khác = serviceFee + otherTax
            BigDecimal otherFees = detail.getServiceFee().add(detail.getOtherTax());
            totalTaxPrice = totalTaxPrice.add(detail.getLicensePlateFee().add(otherFees));
            totalDiscount = totalDiscount.add(detail.getDiscountAmount());
        }

        return OrderDto.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderDetailsList(order.getOrderDetails().stream()
                        .map(od -> OrderDetailsDto.builder()
                                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                                .modelYear(od.getStoreStock().getModelColor().getModel().getModelYear())
                                .seatingCapacity(od.getStoreStock().getModelColor().getModel().getSeatingCapacity())
                                .bodyType(od.getStoreStock().getModelColor().getModel().getBodyType().name())
                                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                                // Vehicle info: detail có thể có nhiều vehicle, lấy vehicle đầu tiên hoặc null
                                .vin(od.getVehicles() != null && !od.getVehicles().isEmpty() ? od.getVehicles().get(0).getVin() : null)
                                .engineNo(od.getVehicles() != null && !od.getVehicles().isEmpty() ? od.getVehicles().get(0).getEngineNo() : null)
                                .batteryNo(od.getVehicles() != null && !od.getVehicles().isEmpty() ? od.getVehicles().get(0).getBatteryNo() : null)
                                .quantity(od.getQuantity()) // Số lượng vehicle trong detail
                                .unitPrice(od.getUnitPrice())
                                .discount(od.getDiscountAmount())
                                .totalTax(od.getLicensePlateFee().add(od.getServiceFee().add(od.getOtherTax()))) // biển số và các loại phí khác
                                .totalPrice(od.getTotalPrice())
                                .build())
                        .toList())
                .totalQuantity(count)
                .totalUnitPrice(totalUnitPrice)
                .totalTaxPrice(totalTaxPrice)
                .totalDiscount(totalDiscount)
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getFullName())
                .staffId(order.getUser().getUserId())
                .staffName(order.getUser().getFullName())
                .storeId(order.getStore().getStoreId())
                .storeName(order.getStore().getStoreName())
                .storeAddress(order.getStore().getAddress())
                .paymentDeadline(order.getPaymentDeadline())
                .build();
    }

    @Override
    public List<GetOrderResponse> getAllOrdersByStore() {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        
        List<Order> orders;
        if (isManager) {
            // Manager: xem tất cả orders trong store
            orders = orderRepository.findByStore_StoreId(store.getStoreId());
        } else {
            // Staff: chỉ xem orders của chính họ trong store
            orders = orderRepository.findByStore_StoreIdAndUser_UserId(store.getStoreId(), currentUser.getUserId());
        }
        
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public void updateOrderStatusWithDeadline(Order order, OrderStatus status, LocalDateTime paymentDeadline) {
        order.setStatus(status);
        order.setPaymentDeadline(paymentDeadline);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

//    private void updateOrderStatusInternal(Order order, OrderStatus status) {
//        // ✅ Khi order được DELIVERED, trừ stock thực tế và unlock reserved
//        if (status == OrderStatus.DELIVERED && order.getStatus() != OrderStatus.DELIVERED) {
//            for (OrderDetail orderDetail : order.getOrderDetails()) {
//                StoreStock stock = orderDetail.getStoreStock();
//
//                // Mỗi detail = 1 vehicle, trừ 1
//                stock.setQuantity(stock.getQuantity() - 1);
//
//                // Unlock reserved quantity: mỗi detail unlock 1
//                stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - 1));
//
//                storeStockService.updateStoreStock(stock);
//            }
//        }
//
//        order.setStatus(status);
//        order.setUpdatedAt(LocalDateTime.now());
//        orderRepository.save(order);
//    }

    @Override
    @Transactional
    public GetOrderResponse confirmOrder(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        // Validation: Order phải thuộc store của user hiện tại
        if (order.getStore() == null || order.getStore().getStoreId() != store.getStoreId()) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        // Validation: Order status phải là DRAFT
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE, 
                "Chỉ có thể xác nhận đơn hàng ở trạng thái DRAFT");
        }

        // không thể xác nhận đơn hàng nếu không có sản phẩm nào
        if (order.getOrderDetails().isEmpty()) {
            throw new AppException(ErrorCode.ORDER_NO_ITEMS);
        }

        // Reserve stock và tự động gán vehicles cho tất cả order details
        // 1 detail có thể có nhiều vehicle (quantity), reserve theo quantity và gán đủ số lượng
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            StoreStock stock = orderDetail.getStoreStock();
            int availableStock = stock.getQuantity() - stock.getReservedQuantity();

            // Check xem có đủ stock để reserve quantity của detail này không
            if (availableStock < orderDetail.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK,
                    String.format("Sản phẩm %s không đủ hàng để xác nhận đơn. Còn %d, yêu cầu %d",
                        stock.getModelColor().getModel().getModelName(),
                        availableStock,
                        orderDetail.getQuantity()));
            }

            // giữ chỗ cho đơn hàng: reserve theo quantity của detail
            stock.setReservedQuantity(stock.getReservedQuantity() + orderDetail.getQuantity());
            storeStockService.updateStoreStock(stock);

            // ========== TỰ ĐỘNG GÁN VEHICLES ==========
            // Lấy danh sách vehicles available theo model+color, sắp xếp theo importDate ASC (cũ nhất trước)
            int modelId = stock.getModelColor().getModel().getModelId();
            int colorId = stock.getModelColor().getColor().getColorId();
            List<Vehicle> availableVehicles = vehicleService.getAvailableVehicleEntitiesByModelAndColor(
                    store.getStoreId(), modelId, colorId);

            // Kiểm tra có đủ vehicles để gán không
            if (availableVehicles.size() < orderDetail.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK,
                    String.format("Không đủ xe có sẵn để gán cho %s. Còn %d xe, yêu cầu %d",
                        stock.getModelColor().getModel().getModelName(),
                        availableVehicles.size(),
                        orderDetail.getQuantity()));
            }

            // Gán vehicles cho order detail (lấy từ cũ nhất đến mới nhất)
            for (int i = 0; i < orderDetail.getQuantity(); i++) {
                Vehicle vehicle = availableVehicles.get(i);
                
                // Gán orderDetail vào vehicle
                vehicle.setOrderDetail(orderDetail);
                
                // Chuyển status từ AVAILABLE -> HOLDING
                vehicle.setStatus(VehicleStatus.HOLDING);
                
                // Update vehicle qua service
                vehicleService.updateVehicleWithOrderDetail(vehicle);
            }
            
            // Cập nhật orderDetail sau khi đã gán tất cả vehicles
            orderDetail.setUpdatedAt(LocalDateTime.now());
            orderDetailService.updateOrderDetail(orderDetail);
        }

        updateOrderStatus(order, OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian để track timeout
        orderRepository.save(order);
        return mapToDto(order);
    }

    @Override
    @Transactional
    public GetOrderResponse markOrderDelivered(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

        // ✅ Manager: có thể mark delivered cho bất kỳ order nào trong store
        // ✅ Staff: chỉ có thể mark delivered cho order của chính họ
        Order order;
        if (isManager) {
            order = orderRepository.findByStore_StoreIdAndOrderId(store.getStoreId(), orderId);
        } else {
            order = orderRepository.findByStore_StoreIdAndUser_UserIdAndOrderId(
                    store.getStoreId(), currentUser.getUserId(), orderId);
        }

        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        // Chỉ có thể giao hàng khi đã thanh toán đầy đủ (FULLY_PAID) hoặc đã ký hợp đồng mua bán (SALE_SIGNED)
        if (order.getStatus() != OrderStatus.FULLY_PAID && order.getStatus() != OrderStatus.SALE_SIGNED) {
            throw new AppException(ErrorCode.ORDER_CANNOT_DELIVER, 
                "Chỉ có thể giao hàng cho đơn hàng đã thanh toán đầy đủ hoặc đã ký hợp đồng mua bán");
        }

        updateOrderStatus(order, OrderStatus.DELIVERED);
        return mapToDto(order);
    }

    @Override
    @Transactional
    public GetOrderResponse confirmDepositPayment(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

        // ✅ Manager: có thể xác nhận cho bất kỳ order nào trong store
        // ✅ Staff: chỉ có thể xác nhận cho order của chính họ
        Order order;
        if (isManager) {
            order = orderRepository.findByStore_StoreIdAndOrderId(store.getStoreId(), orderId);
        } else {
            order = orderRepository.findByStore_StoreIdAndUser_UserIdAndOrderId(
                    store.getStoreId(), currentUser.getUserId(), orderId);
        }

        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        // Validate order status phải là PENDING_DEPOSIT
        if (order.getStatus() != OrderStatus.PENDING_DEPOSIT) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE, 
                "Chỉ có thể xác nhận thanh toán đặt cọc cho đơn hàng ở trạng thái chờ đặt cọc");
        }

        // Kiểm tra xem đã có payment deposit completed chưa
        List<Payment> depositPayments = paymentService.getPaymentsByOrderAndPaymentType(orderId, PaymentType.DEPOSIT);
        boolean hasCompletedDeposit = depositPayments.stream()
            .anyMatch(p -> p.getStatus() == PaymentStatus.COMPLETED);
        
        if (!hasCompletedDeposit) {
            throw new AppException(ErrorCode.DEPOSIT_PAYMENT_NOT_COMPLETED, 
                "Chưa có giao dịch đặt cọc được thanh toán thành công");
        }

        // Update order status thành DEPOSIT_PAID
        updateOrderStatus(order, OrderStatus.DEPOSIT_PAID);
        
        // Nếu đã có contract đặt cọc, update contract status thành DEPOSIT_PAID
        if (contractService.hasDepositContract(orderId)) {
            Contract depositContract = contractService.getDepositContractByOrderId(orderId);
            contractService.updateContractStatus(depositContract, ContractStatus.DEPOSIT_PAID);
        }
        
        return mapToDto(order);
    }

    // không xóa được đơn hàng đã ký hợp đồng, đã thanh toán đặt cọc, đã thanh toán đầy đủ, đã giao hàng
    @Override
    @Transactional
    public void deleteOrder(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        
        Order order;
        if (isManager) {
            // Manager: có thể xóa order trong store
            order = orderRepository.findByStore_StoreIdAndOrderId(store.getStoreId(), orderId);
        } else {
            // Staff: chỉ có thể xóa order của chính họ
            order = orderRepository.findByStore_StoreIdAndUser_UserIdAndOrderId(
                    store.getStoreId(), currentUser.getUserId(), orderId);
        }
        
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        
        if (order.getStatus() == OrderStatus.DEPOSIT_SIGNED
                || order.getStatus() == OrderStatus.SALE_SIGNED
                || order.getStatus() == OrderStatus.DEPOSIT_PAID
                || order.getStatus() == OrderStatus.FULLY_PAID
                || order.getStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE);
        }

        // ✅ Unlock stock nếu order đã được CONFIRMED
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            unlockStockForOrder(order);
        }

        orderRepository.delete(order);
    }

    // ✅ Method để unlock stock khi order bị hủy
    private void unlockStockForOrder(Order order) {
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            StoreStock stock = orderDetail.getStoreStock();
            int currentReserved = stock.getReservedQuantity();

            // Unlock theo quantity của detail
            stock.setReservedQuantity(Math.max(0, currentReserved - orderDetail.getQuantity()));
            storeStockService.updateStoreStock(stock);
        }
    }

//    @Override
//    public OrderDto updateOrderStatus(int orderId, OrderStatus status) {
//        Order order = orderRepository.findById(orderId).orElse(null);
//        if (order == null) {
//            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
//        }
//
//        order.setStatus(status);
//        order.setUpdatedAt(LocalDateTime.now());
//        Order savedOrder = orderRepository.save(order);
//        return mapToDto(savedOrder);
//    }
//
    @Override
    public List<GetOrderResponse> getOrdersByCustomerId(int customerId) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        List<Order> orders = orderRepository.findByCustomer_CustomerIdAndStore_StoreId(customerId, store.getStoreId());
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public StaffMonthlyOrdersResponse getOrdersByStaffId(int staffId) {
        // Validate staff exists
        User staff = userService.getUserEntityById(staffId);
        
        // Lấy tháng hiện tại
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        
        // Lấy orders của staff trong tháng hiện tại với status FULLY_PAID hoặc DELIVERED
        List<OrderStatus> allowedStatuses = List.of(OrderStatus.FULLY_PAID, OrderStatus.DELIVERED);
        List<Order> orders = orderRepository.findByUser_UserIdAndStatusInAndOrderDateBetween(
                staffId, allowedStatuses, startOfMonth, startOfNextMonth);
        
        // Convert to DTO
        List<GetOrderResponse> orderResponses = orders.stream()
                .map(this::mapToDto)
                .toList();
        
        // Tính tổng số orders
        int totalOrders = orders.size();
        
        // Tính doanh thu tháng (tổng totalPayment)
        BigDecimal monthlyRevenue = orders.stream()
                .map(Order::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return StaffMonthlyOrdersResponse.builder()
                .staffId(staffId)
                .staffName(staff.getFullName())
                .orders(orderResponses)
                .totalOrders(totalOrders)
                .monthlyRevenue(monthlyRevenue)
                .build();
    }

    @Override
    public StoreMonthlyRevenueResponse getStoreMonthlyRevenue() {
        // Lấy user hiện tại và kiểm tra quyền
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        
        // Kiểm tra user có phải là manager không
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        if (!isManager) {
            throw new AppException(ErrorCode.UNAUTHORIZED_UPDATE_PROMOTION); // Có thể tạo error code riêng sau
        }
        
        // Lấy tháng hiện tại
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        
        // Lấy tất cả orders FULLY_PAID của store trong tháng hiện tại
        List<Order> fullyPaidOrders = orderRepository.findByStore_StoreIdAndStatusAndOrderDateBetween(
                store.getStoreId(),
                OrderStatus.FULLY_PAID,
                startOfMonth,
                startOfNextMonth
        );
        
        // Tính tổng số đơn hàng
        long totalOrders = fullyPaidOrders.size();
        
        // Tính tổng doanh thu (totalPayment của các orders)
        BigDecimal totalRevenue = fullyPaidOrders.stream()
                .map(Order::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return StoreMonthlyRevenueResponse.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    public List<Order> getOrdersByStoreIdAndStatusAndDateRange(int storeId, OrderStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByStore_StoreIdAndStatusAndOrderDateBetween(storeId, status, startDate, endDate);
    }

//    @Override
//    public List<GetOrderResponse> getOrdersByStatus(OrderStatus status) {
//        return orderRepository.findByStatus(OrderStatus.valueOf(status.toUpperCase())).stream()
//                .map(this::mapToDto)
//                .toList();
//    }
//
//    @Override
//    public List<GetOrderResponse> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
//        // Convert LocalDate to LocalDateTime for the full day range
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
//        return orderRepository.findByOrderDateBetween(startDateTime, endDateTime).stream()
//                .map(this::mapToDto)
//                .toList();
//    }

//    @Override
//    public List<OrderDto> searchOrdersByCustomerPhone(String phone) {
//        return orderRepository.findByCustomerPhone(phone).stream()
//                .map(this::mapToDto)
//                .toList();
//    }
//
//    @Override
//    public long countOrdersByStatus(OrderStatus status) {
//
//        return orderRepository.countByStatus(status);
//    }
//
//    @Override
//    public List<OrderDto> getRecentOrders() {
//        return orderRepository.findTop10ByOrderByOrderDateDesc()
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//    }

    @Override
    public Order getOrderEntityById(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }
        return order;
    }

    /**
     * Helper method để map OrderDetail thành GetOrderDetailsResponse (bao gồm list vehicles)
     */
    private GetOrderDetailsResponse mapOrderDetailToResponse(OrderDetail od) {
        // Map list vehicles từ order detail sang VehicleSimpleResponse (chỉ thông tin cơ bản)
        List<VehicleSimpleResponse> vehicles = null;
        if (od.getVehicles() != null && !od.getVehicles().isEmpty()) {
            vehicles = od.getVehicles().stream()
                    .map(v -> VehicleSimpleResponse.builder()
                            .vehicleId(v.getVehicleId())
                            .vin(v.getVin())
                            .engineNo(v.getEngineNo())
                            .batteryNo(v.getBatteryNo())
                            .status(v.getStatus() != null ? v.getStatus().name() : null)
                            .build())
                    .toList();
        }
        
        return GetOrderDetailsResponse.builder()
                .orderDetailId(od.getId())
                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                .unitPrice(od.getUnitPrice())
                .quantity(od.getQuantity()) // Số lượng vehicle trong detail
                .licensePlateFee(od.getLicensePlateFee()) // phí biển số
                .serviceFee(od.getServiceFee()) // phí đăng ký biển số
                .otherTax(od.getOtherTax()) // thuế khác
                .otherFees(od.getServiceFee().add(od.getOtherTax())) // phí khác (gồm phí đăng ký biển số + thuế khác)
                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : null)
                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                .discountAmount(od.getDiscountAmount())
                .totalPrice(od.getTotalPrice())
                .vehicles(vehicles) // Danh sách vehicles được gán vào order detail
                .build();
    }

    // Helper method to convert Entity to DTO
    private GetOrderResponse mapToDto(Order order) {
        BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal remainingAmount = order.getTotalPayment().subtract(paidAmount);
        
        return GetOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .getOrderDetailsResponses(order.getOrderDetails().stream()
                        .map(this::mapOrderDetailToResponse)
                        .toList())
                .totalPrice(order.getTotalPrice()) // giá trị trước thuế và khuyến mãi
                .totalTaxPrice(order.getTotalTaxPrice())
                .totalPromotionAmount(order.getTotalPromotionAmount())
                .totalPayment(order.getTotalPayment())
                .paidAmount(paidAmount)
                .remainingAmount(remainingAmount)
                .status(order.getStatus().name())
//                .contractId(order.getContract() != null ? order.getContract().getContractId() : 0)
//                .contractCode(order.getContract() != null ? order.getContract().getContractCode() : null)
//                .urlContractFile(order.getContract() != null ? order.getContract().getContractFileUrl() : null)
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getFullName())
                .customerPhone(order.getCustomer().getPhone())
                .feedbackId(order.getFeedback() != null ? order.getFeedback().getFeedbackId() : 0)
                .staffId(order.getUser().getUserId())
                .staffName(order.getUser().getFullName())
                .storeId(order.getStore().getStoreId())
                .storeName(order.getStore().getStoreName())
                .orderDate(order.getOrderDate())
                .paymentDeadline(order.getPaymentDeadline())
                .build();
    }

    // ✅ Auto-cancel DRAFT orders sau 24 giờ
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    @Transactional
    public void autoCancelExpiredDraftOrders() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(24); // 24 giờ

        List<Order> expiredOrders = orderRepository.findByStatusAndOrderDateBefore(
            OrderStatus.DRAFT,
            expiryTime
        );

        for (Order order : expiredOrders) {
            // DRAFT orders chưa reserve stock nên không cần unlock
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    // ✅ Auto-cancel CONFIRMED orders không thanh toán đặt cọc sau 48 giờ
    // @Scheduled(fixedRate = 3600000) // Mỗi giờ
    // @Transactional
    // public void autoCancelUnpaidConfirmedOrders() {
    //     LocalDateTime expiryTime = LocalDateTime.now().minusHours(48); // 48 giờ sau khi CONFIRMED

    //     List<Order> unpaidOrders = orderRepository.findByStatusAndUpdatedAtBefore(
    //         OrderStatus.CONFIRMED,
    //         expiryTime
    //     );

    //     for (Order order : unpaidOrders) {
    //         // Kiểm tra xem đã có payment deposit chưa
    //         boolean hasDepositPayment = false;
            
    //         // Tìm deposit contract của order này
    //         Optional<Contract> depositContract = contractRepository.findByOrder_OrderIdAndContractType(
    //             order.getOrderId(), ContractType.DEPOSIT);
            
    //         if (depositContract.isPresent()) {
    //             Contract contract = depositContract.get();
    //             // Kiểm tra contract status
    //             if (contract.getStatus() == ContractStatus.DEPOSIT_PAID
    //                     || contract.getStatus() == ContractStatus.FULLY_PAID
    //                     || contract.getStatus() == ContractStatus.COMPLETED) {
    //                 hasDepositPayment = true;
    //             }
    //         }

    //         // Nếu chưa có deposit payment thì hủy và unlock stock
    //         if (!hasDepositPayment) {
    //             unlockStockForOrder(order);
    //             order.setStatus(OrderStatus.CANCELLED);
    //             order.setUpdatedAt(LocalDateTime.now());
    //             orderRepository.save(order);
    //         }
    //     }
    // }

    // // ✅ Auto-cancel CONTRACT_PENDING orders không ký hợp đồng sau 72 giờ
    // @Scheduled(fixedRate = 3600000) // Mỗi giờ
    // @Transactional
    // public void autoCancelUnsignedContractOrders() {
    //     LocalDateTime expiryTime = LocalDateTime.now().minusHours(72); // 72 giờ sau khi CONTRACT_PENDING

    //     List<Order> unsignedOrders = orderRepository.findByStatusAndUpdatedAtBefore(
    //         OrderStatus.CONTRACT_PENDING,
    //         expiryTime
    //     );

    //     for (Order order : unsignedOrders) {
    //         // Kiểm tra xem contract đã được ký chưa
    //         boolean isContractSigned = false;
            
    //         // Tìm deposit contract của order này
    //         Optional<Contract> depositContract = contractRepository.findByOrder_OrderIdAndContractType(
    //             order.getOrderId(), ContractType.DEPOSIT);
            
    //         if (depositContract.isPresent()) {
    //             Contract contract = depositContract.get();
    //             if (contract.getStatus() == ContractStatus.SIGNED
    //                     || contract.getStatus() == ContractStatus.DEPOSIT_PAID
    //                     || contract.getStatus() == ContractStatus.FULLY_PAID
    //                     || contract.getStatus() == ContractStatus.COMPLETED) {
    //                 isContractSigned = true;
    //             }
    //         }

    //         // Nếu chưa ký hợp đồng thì hủy và unlock stock
    //         if (!isContractSigned) {
    //             unlockStockForOrder(order);
    //             order.setStatus(OrderStatus.CANCELLED);
    //             order.setUpdatedAt(LocalDateTime.now());
    //             orderRepository.save(order);
    //         }
    //     }
    // }

    // ✅ Auto-cancel PENDING_DEPOSIT orders quá 4 giờ chưa thanh toán đặt cọc
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    @Transactional
    public void autoCancelExpiredPendingDepositOrders() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Checking PENDING_DEPOSIT orders with expired payment deadline at {}", now);

        // Tìm các orders có status PENDING_DEPOSIT và đã quá hạn thanh toán (4 giờ)
        List<Order> expiredOrders = orderRepository.findOrdersWithExpiredPaymentDeadline(
            OrderStatus.PENDING_DEPOSIT, 
            now
        );

        for (Order order : expiredOrders) {
            // Kiểm tra xem đã có deposit contract (hợp đồng đặt cọc) chưa
            // Nếu đã có deposit contract thì có nghĩa là đã thanh toán và tạo hợp đồng
            boolean hasDepositContract = contractService.hasDepositContract(order.getOrderId());

            // Nếu chưa có deposit contract (chưa thanh toán) thì hủy và unlock stock
            if (!hasDepositContract) {
                logger.info("Cancelling order {} due to expired deposit payment deadline (no payment within 4 hours)", order.getOrderId());
                unlockStockForOrder(order);
                order.setStatus(OrderStatus.CANCELLED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            }
        }
    }

    // ✅ Auto-expire orders quá hạn thanh toán (7 ngày sau khi đặt cọc)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh") // Chạy mỗi ngày lúc 00:00
    @Transactional
    public void autoExpireOrdersWithExpiredPaymentDeadline() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Checking orders with expired payment deadline at {}", now);

        // Tìm các orders có status DEPOSIT_PAID hoặc DEPOSIT_SIGNED và đã quá hạn thanh toán
        List<Order> expiredDepositPaidOrders = orderRepository.findOrdersWithExpiredPaymentDeadline(
            OrderStatus.DEPOSIT_PAID, 
            now
        );
        List<Order> expiredDepositSignedOrders = orderRepository.findOrdersWithExpiredPaymentDeadline(
            OrderStatus.DEPOSIT_SIGNED, 
            now
        );
        
        List<Order> expiredOrders = new java.util.ArrayList<>();
        expiredOrders.addAll(expiredDepositPaidOrders);
        expiredOrders.addAll(expiredDepositSignedOrders);

        for (Order order : expiredOrders) {
            // Unlock reserved stock
            unlockStockForOrder(order);
            
            // Chuyển vehicle về AVAILABLE lại
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                if (orderDetail.getVehicles() != null && !orderDetail.getVehicles().isEmpty()) {
                    for (Vehicle vehicle : orderDetail.getVehicles()) {
                        vehicleService.updateVehicleStatusById(
                            vehicle.getVehicleId(), 
                            VehicleStatus.AVAILABLE
                        );
                    }
                }
            }
            
            // Cập nhật status thành EXPIRED
            order.setStatus(OrderStatus.EXPIRED);
            order.setUpdatedAt(now);
            orderRepository.save(order);
            logger.info("Order {} has been expired due to payment deadline passed. Stock unlocked and vehicles set to AVAILABLE", order.getOrderCode());
        }

        logger.info("Expired {} orders with payment deadline passed", expiredOrders.size());
    }
}