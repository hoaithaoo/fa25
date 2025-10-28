package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailDto;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderDetailsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderWithItemsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.stock.StockValidationRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderWithItemsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;
import swp391.fa25.saleElectricVehicle.payload.response.stock.StockValidationResponse;
import swp391.fa25.saleElectricVehicle.repository.*;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreStockService storeStockService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ColorService colorService;

    @Autowired
    private ModelColorService modelColorService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    private final double VAT_AMOUNT_RATE = (double) 10 /100; // 10%
    private final BigDecimal LICENSE_PLATE_AMOUNT_20M = BigDecimal.valueOf(20000000); // 20 millions VND
    private final BigDecimal LICENSE_PLATE_AMOUNT_200K = BigDecimal.valueOf(200000); // 20 thousands VND
    private final BigDecimal REGISTRATION_FEE_AMOUNT = BigDecimal.valueOf(1500000); // 1.5 millions VND


    @Override
    public StockValidationResponse validateStockAvailability(StockValidationRequest request) {
        // 1. Validate model exists
        Model model = modelService.getModelEntityById(request.getModelId());

        // 2. Validate color exists
        Color color = colorService.getColorEntityById(request.getColorId());

        ModelColor modelColor = modelColorService
                .getModelColorEntityByModelIdAndColorId(
                        model.getModelId(),
                        color.getColorId()
                );

        // 3. Get store of current user
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // 4. Get stock from warehouse
        StoreStock stock = storeStockService
                .getStoreStockByStoreIdAndModelColorId(
                        store.getStoreId(),
                        modelColor.getModelColorId()
                );

        // 5. Check quantity
        validateStockAvailability(stock, request.getQuantity());

        // 6. Apply promotion if any (NOT affecting stock validation)
        if (request.getPromotionId() > 0) {
            Promotion promotion = promotionService.getPromotionEntityById(request.getPromotionId());
        }

        // 6. Return success validation (NO DB WRITE)
        return StockValidationResponse.builder()
                .modelId(model.getModelId())
                .modelName(model.getModelName())
                .colorId(color.getColorId())
                .colorName(color.getColorName())
                .requestedQuantity(request.getQuantity())
                .promotionId(request.getPromotionId())
                .promotionName(request.getPromotionId() > 0 ?
                        promotionService.getPromotionEntityById(request.getPromotionId()).getPromotionName() : null)
                .build();
//                .availableStock(stock.getQuantity())
//                .isAvailable(true)
    }

// =============== CRUD OPERATIONS ===============
    @Transactional
    @Override
    public CreateOrderWithItemsResponse createOrderDetail(CreateOrderWithItemsRequest request) {
        // Validate dependencies
        Order order = orderService.getOrderEntityById(request.getOrderId());

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE);
        }

//        List<OrderDetail> orderDetails = new ArrayList<>();
        List<OrderDetail> orderDetails = order.getOrderDetails();
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPromotions = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;

        for (int i = 0; i < request.getOrderDetails().size(); i++) {
            CreateOrderDetailsRequest itemReq = request.getOrderDetails().get(i);

            // Get model
            Model model = modelService.getModelEntityById(itemReq.getModelId());

            // Get color
            Color color = colorService.getColorEntityById(itemReq.getColorId());

            // Get modelColor
            ModelColor modelColor = modelColorService
                    .getModelColorEntityByModelIdAndColorId(
                            model.getModelId(),
                            color.getColorId()
                    );

            User staff = userService.getCurrentUserEntity();
            Store store = storeService.getCurrentStoreEntity(staff.getUserId());

            // Get stock WITH PESSIMISTIC LOCK
            // LOCK row này để tránh race condition
            StoreStock stock = storeStockService.getStoreStockByStoreIdAndModelColorIdWithLock(
                    store.getStoreId(),
                    modelColor.getModelColorId()
            );

            // Validate stock availability again
            validateStockAvailability(stock, itemReq.getQuantity());
            // Calculate total order price before tax and discounts
            totalOrderPrice = totalOrderPrice.add(
                    stock.getPriceOfStore().multiply(BigDecimal.valueOf(itemReq.getQuantity()))
            );

            // Calculate VAT amount
            BigDecimal vatAmount = stock.getPriceOfStore().multiply(BigDecimal.valueOf(VAT_AMOUNT_RATE)).multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            // Check province for license plate fee
            BigDecimal licensePlateFee = LICENSE_PLATE_AMOUNT_200K.multiply(BigDecimal.valueOf(itemReq.getQuantity())); // Default 200K/vehicle
            if (store.getProvinceName().equalsIgnoreCase("TP. Hồ Chí Minh") || store.getProvinceName().equalsIgnoreCase("Hà Nội")) {
                licensePlateFee = (LICENSE_PLATE_AMOUNT_20M).multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            }
            totalTax = totalTax.add(vatAmount).add(licensePlateFee).add(REGISTRATION_FEE_AMOUNT);

            // Calculate discount amount
            Promotion promotion = null;
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (itemReq.getPromotionId() > 0) {
                promotion = promotionService.getPromotionEntityById(itemReq.getPromotionId());
                    // Giả sử promotion là giảm giá theo phần trăm
                    if (promotion.getPromotionType().toString().equals("PERCENTAGE")) {
                        discountAmount = stock.getPriceOfStore()
                                .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                                .multiply(promotion.getAmount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    } else if (promotion.getPromotionType().toString().equals("FIXED_AMOUNT")) {
                        discountAmount = promotion.getAmount().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                    }
            }

            totalPromotions = totalPromotions.add(discountAmount);

            // Calculate total price
            price = calculateTotalPrice(
                    stock.getPriceOfStore(),
                    itemReq.getQuantity(),
                    vatAmount,
                    licensePlateFee,
                    REGISTRATION_FEE_AMOUNT,
                    discountAmount
            );
            // calculate final amount
            finalAmount = finalAmount.add(price);

            // Create OrderDetail
            OrderDetail orderDetail = OrderDetail.builder()
                    .unitPrice(stock.getPriceOfStore())
                    .quantity(itemReq.getQuantity())
                    .vatAmount(vatAmount)
                    .licensePlateFee(licensePlateFee)
                    .registrationFee(REGISTRATION_FEE_AMOUNT)
                    .discountAmount(discountAmount)
                    .totalPrice(price) // price after tax and discount
                    .createdAt(LocalDateTime.now())
                    .order(order)
                    .storeStock(stock)
                    .promotion(promotion)
                    .build();

            // Save OrderDetail
            OrderDetail saved = orderDetailRepository.save(orderDetail);
            orderDetails.add(saved);

            // Update stock quantity
            int newQuantity = stock.getQuantity() - itemReq.getQuantity();
            storeStockService.updateQuantity(stock.getStockId(), newQuantity);
        }

        order.setTotalPrice(totalOrderPrice);
        order.setTotalTaxPrice(totalTax);
        order.setTotalPromotionAmount(totalPromotions);
        order.setTotalPayment(finalAmount);
        orderService.updateAfterDetailChange(order);

        return CreateOrderWithItemsResponse.builder()
                .orderDetailsResponses(
                        orderDetails.stream().map(od -> CreateOrderDetailsResponse.builder()
                                .orderDetailId(od.getId())
                                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                                .unitPrice(od.getUnitPrice())
                                .quantity(od.getQuantity())
                                .vatAmount(od.getVatAmount())
                                .licensePlateFee(od.getLicensePlateFee())
                                .registrationFee(od.getRegistrationFee())
                                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : 0)
                                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                                .discountAmount(od.getDiscountAmount())
                                .totalPrice(od.getTotalPrice())
                                .build()
                        ).toList()
                )
                .build();
    }


    @Override
    public GetOrderDetailsResponse getOrderDetailById(int id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
        return mapToDto(orderDetail);
    }

//    @Override
//    public List<OrderDetailDto> getAllOrderDetails() {
//        return orderDetailRepository.findAll().stream()
//                .map(this::mapToDto)
//                .toList();
//    }
//
//    @Override
//    public OrderDetailDto updateOrderDetail(int id, OrderDetailDto orderDetailDto) {
//        OrderDetail orderDetail = orderDetailRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
//
//        // Handle quantity change (stock adjustment)
//        if (orderDetailDto.getQuantity() != orderDetail.getQuantity()) {
//            int oldQuantity = orderDetail.getQuantity();
//            int newQuantity = orderDetailDto.getQuantity();
//            int stockDifference = oldQuantity - newQuantity;
//
//            // Update stock
//            StoreStock storeStock = orderDetail.getStoreStock();
//            storeStock.setQuantity(storeStock.getQuantity() + stockDifference);
//            storeStockRepository.save(storeStock);
//        }
//
//        // Update fields
//        orderDetail.setUnitPrice(orderDetailDto.getUnitPrice());
//        orderDetail.setQuantity(orderDetailDto.getQuantity());
//        orderDetail.setVatAmount(orderDetailDto.getVatAmount());
//        orderDetail.setLicensePlateFee(orderDetailDto.getLicensePlateFee());
//        orderDetail.setRegistrationFee(orderDetailDto.getRegistrationFee());
//        orderDetail.setDiscountAmount(orderDetailDto.getDiscountAmount());
//
//        // Recalculate total price
//        BigDecimal newTotalPrice = calculateTotalPrice(
//                orderDetailDto.getUnitPrice(),
//                orderDetailDto.getQuantity(),
//                orderDetailDto.getVatAmount(),
//                orderDetailDto.getLicensePlateFee(),
//                orderDetailDto.getRegistrationFee(),
//                orderDetailDto.getDiscountAmount()
//        );
//        orderDetail.setTotalPrice(newTotalPrice);
//        orderDetail.setUpdatedAt(LocalDateTime.now());
//
//        OrderDetail saved = orderDetailRepository.save(orderDetail);
//        return mapToDto(saved);
//    }
//
//    @Override
//    public void deleteOrderDetail(int id) {
//        OrderDetail orderDetail = orderDetailRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
//
//        // Restore stock quantity
//        StoreStock storeStock = orderDetail.getStoreStock();
//        storeStock.setQuantity(storeStock.getQuantity() + orderDetail.getQuantity());
//        storeStockRepository.save(storeStock);
//
//        orderDetailRepository.delete(orderDetail);
//    }
//
//    // =============== BUSINESS OPERATIONS ===============
//
//    @Override
//    public List<OrderDetailDto> getOrderDetailsByOrderId(int orderId) {
//        return orderDetailRepository.findByOrder_OrderId(orderId).stream()
//                .map(this::mapToDto)
//                .toList();
//    }
//
//    @Override
//    public OrderDetailDto updateQuantity(int id, int quantity) {
//        OrderDetail orderDetail = orderDetailRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
//
//        int oldQuantity = orderDetail.getQuantity();
//        int stockDifference = oldQuantity - quantity;
//
//        // Check stock availability for increase
//        if (quantity > oldQuantity) {
//            if (!validateStockAvailability(orderDetail.getStoreStock().getStockId(), quantity - oldQuantity)) {
//                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
//            }
//        }
//
//        // Update stock
//        StoreStock storeStock = orderDetail.getStoreStock();
//        storeStock.setQuantity(storeStock.getQuantity() + stockDifference);
//        storeStockRepository.save(storeStock);
//
//        // Update order detail
//        orderDetail.setQuantity(quantity);
//
//        // Recalculate total price
//        BigDecimal newTotalPrice = calculateTotalPrice(
//                orderDetail.getUnitPrice(),
//                quantity,
//                orderDetail.getVatAmount(),
//                orderDetail.getLicensePlateFee(),
//                orderDetail.getRegistrationFee(),
//                orderDetail.getDiscountAmount()
//        );
//        orderDetail.setTotalPrice(newTotalPrice);
//        orderDetail.setUpdatedAt(LocalDateTime.now());
//
//        OrderDetail saved = orderDetailRepository.save(orderDetail);
//        return mapToDto(saved);
//    }
//
//    // =============== VALIDATION ===============
//
    private void validateStockAvailability(StoreStock stock, int requestedQuantity) {
        if (stock.getQuantity() < requestedQuantity) {

            // Throw exception với thông tin chi tiết
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, String.format(
                    ". Sản phẩm %s không đủ hàng. Còn %d, yêu cầu %d",
                    stock.getModelColor().getModel().getModelName(),
                    stock.getQuantity(),
                    requestedQuantity));
        }
    }

    //
//    // =============== CALCULATION ===============
//
    @Override
    public BigDecimal calculateTotalPrice(BigDecimal priceOfStore, int quantity,
                                          BigDecimal vatAmount, BigDecimal licensePlateFee,
                                          BigDecimal registrationFee, BigDecimal discountAmount) {
        return priceOfStore
                .multiply(BigDecimal.valueOf(quantity))
                .add(vatAmount)
                .add(licensePlateFee)
                .add(registrationFee)
                .subtract(discountAmount);
    }
//
//    // =============== HELPER METHODS ===============
//
    private GetOrderDetailsResponse mapToDto(OrderDetail od) {
        return GetOrderDetailsResponse.builder()
                .orderDetailId(od.getId())
                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                .unitPrice(od.getUnitPrice())
                .quantity(od.getQuantity())
                .vatAmount(od.getVatAmount())
                .licensePlateFee(od.getLicensePlateFee())
                .registrationFee(od.getRegistrationFee())
                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : null)
                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                .discountAmount(od.getDiscountAmount())
                .totalPrice(od.getTotalPrice())
                .build();
//        OrderDetailDto dto = OrderDetailDto.builder()
//                .id(entity.getId())
//                .unitPrice(entity.getUnitPrice())
//                .quantity(entity.getQuantity())
//                .vatAmount(entity.getVatAmount())
//                .licensePlateFee(entity.getLicensePlateFee())
//                .registrationFee(entity.getRegistrationFee())
//                .discountAmount(entity.getDiscountAmount())
//                .totalPrice(entity.getTotalPrice())
//                .createdAt(entity.getCreatedAt())
//                .updatedAt(entity.getUpdatedAt())
//                .orderId(entity.getOrder().getOrderId())
//                .storeStockId(entity.getStoreStock().getStockId())
//                .promotionId(entity.getPromotion() != null ? entity.getPromotion().getPromotionId() : 0)
//                .build();
//
//        // ✅ Set display fields safely - KHÔNG CÓ BRAND:
//        if (entity.getStoreStock() != null && entity.getStoreStock().getModelColor() != null) {
//            ModelColor modelColor = entity.getStoreStock().getModelColor();
//
//            if (modelColor.getModel() != null) {
//                Model model = modelColor.getModel();
//                dto.setModelName(model.getModelName()); // ✅ CÓ
//                // ❌ LOẠI BỎ: dto.setBrandName(...) - KHÔNG CÓ BRAND!
//            }
//
//            if (modelColor.getColor() != null) {
//                dto.setColorName(modelColor.getColor().getColorName());
//            }
//
//            dto.setModelPrice(entity.getStoreStock().getPriceOfStore());
//            dto.setAvailableStock(entity.getStoreStock().getQuantity());
//        }
//
//        // Set order info safely
//        if (entity.getOrder() != null && entity.getOrder().getStatus() != null) {
//            dto.setOrderStatus(entity.getOrder().getStatus().toString());
//
//            if (entity.getOrder().getCustomer() != null) {
//                dto.setCustomerName(entity.getOrder().getCustomer().getFullName());
//                dto.setCustomerPhone(entity.getOrder().getCustomer().getPhone());
//            }
//        }
//
//        // Set promotion info safely
//        if (entity.getPromotion() != null) {
//            dto.setPromotionName(entity.getPromotion().getPromotionName());
//            dto.setPromotionType(entity.getPromotion().getPromotionType().toString());
//        }
//
//        // Set calculated fields
//        dto.setSubtotal(entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity())));
//        dto.setTotalFees(entity.getLicensePlateFee().add(entity.getRegistrationFee()));
//        dto.setTotalTax(entity.getVatAmount());
//        dto.setPriceBeforeDiscount(dto.getSubtotal().add(dto.getTotalFees()).add(dto.getTotalTax()));
//        dto.setFinalAmount(entity.getTotalPrice());
//
//        // Set display text
//        String modelName = dto.getModelName() != null ? dto.getModelName() : "Unknown Model";
//        String colorName = dto.getColorName() != null ? dto.getColorName() : "Unknown Color";
//        dto.setDisplayText(String.format("%s - %s (Qty: %d)", modelName, colorName, dto.getQuantity()));
//
//        return dto;
    }
}