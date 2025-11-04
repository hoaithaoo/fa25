package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderDetailsRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.CreateOrderDetailsResponse;
import swp391.fa25.saleElectricVehicle.repository.*;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // =============== CRUD OPERATIONS ===============

    @Override
    @Transactional  // đảm bảo atomicity
    public CreateOrderDetailsResponse createOrderDetail(CreateOrderDetailsRequest request) {
        // 1. Validate Order existence và status
        Order order = orderService.getOrderEntityById(request.getOrderId());
        validateOrderStatus(order);

        // 2. Validate Model và Color
        Model model = modelService.getModelEntityById(request.getModelId());
        Color color = colorService.getColorEntityById(request.getColorId());

        // 3. Validate ModelColor combination exists
        ModelColor modelColor = modelColorService.getModelColorEntityByModelIdAndColorId(
                model.getModelId(),
                color.getColorId()
        );

        // 4. Get current user's store
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // 5. Get and validate stock with pessimistic lock
        StoreStock storeStock = storeStockService
                .getStoreStockByStoreIdAndModelColorId(
                        store.getStoreId(),
                        modelColor.getModelColorId()
                );

        // 6. Validate stock availability
        validateStockAvailability(storeStock, request.getQuantity());

        // 7. Validate duplicate item in order
        if (orderDetailRepository.existsByOrderIdAndModelColorId(
                order.getOrderId(), modelColor.getModelColorId())) {
            throw new AppException(ErrorCode.DUPLICATE_ORDER_ITEM);
        }

        // 8. Get promotion if exists
        Promotion promotion = null;
        if (request.getPromotionId() != null && request.getPromotionId() > 0) {
            promotion = promotionService.getPromotionEntityById(request.getPromotionId());

            // Validate promotion is active
            if (!isPromotionValid(promotion)) {
                throw new AppException(ErrorCode.PROMOTION_EXPIRED);
            }
        }

        // 9. Calculate total price
        BigDecimal totalPrice = calculateTotalPrice(
                request.getUnitPrice(),
                request.getQuantity(),
                request.getVatAmount(),
                request.getLicensePlateFee(),
                request.getRegistrationFee(),
                request.getDiscountAmount()
        );

        // 10. Create order detail
        OrderDetail orderDetail = OrderDetail.builder()
                .unitPrice(request.getUnitPrice())
                .quantity(request.getQuantity())
                .vatAmount(request.getVatAmount())
                .licensePlateFee(request.getLicensePlateFee())
                .registrationFee(request.getRegistrationFee())
                .discountAmount(request.getDiscountAmount())
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .order(order)
                .storeStock(storeStock)
                .promotion(promotion)
                .build();

        OrderDetail saved = orderDetailRepository.save(orderDetail);

        // 11. Update stock quantity
        storeStock.setQuantity(storeStock.getQuantity() - request.getQuantity());
        storeStockRepository.save(storeStock);

        // 12. Update order total amount
        updateOrderTotalAmount(order);

        return mapToDto(saved);
    }

    //
//    @Override
//    public OrderDetailDto getOrderDetailById(int id) {
//        OrderDetail orderDetail = orderDetailRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
//        return mapToDto(orderDetail);
//    }
//
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
    private void validateOrderStatus(Order order) {
        // Chỉ cho phép thêm item khi order ở trạng thái DRAFT hoặc PENDING
        if (order.getStatus() != OrderStatus.DRAFT &&
                order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE);
        }
    }

    public void validateStockAvailability(StoreStock storeStock, int requestedQuantity) {
        if (storeStock.getQuantity() < requestedQuantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }

    private boolean isPromotionValid(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();
        return promotion.getStartDate().isBefore(now) &&
                promotion.getEndDate().isAfter(now) &&
                promotion.isActive();
    }
//
//    // =============== CALCULATION ===============
//
//    @Override
//    public BigDecimal calculateTotalPrice(BigDecimal unitPrice, int quantity,
//                                          BigDecimal vatAmount, BigDecimal licensePlateFee,
//                                          BigDecimal registrationFee, BigDecimal discountAmount) {
//        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
//        BigDecimal fees = licensePlateFee.add(registrationFee);
//        BigDecimal beforeDiscount = subtotal.add(vatAmount).add(fees);
//        return beforeDiscount.subtract(discountAmount);
//    }
//
//    // =============== HELPER METHODS ===============
//
//    private OrderDetailDto mapToDto(OrderDetail entity) {
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
//    }
}