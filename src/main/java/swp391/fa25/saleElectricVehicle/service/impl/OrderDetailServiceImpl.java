package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailDto;
import swp391.fa25.saleElectricVehicle.repository.*;
import swp391.fa25.saleElectricVehicle.service.OrderDetailService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StoreStockRepository storeStockRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    // =============== CRUD OPERATIONS ===============

    @Override
    public OrderDetailDto createOrderDetail(OrderDetailDto orderDetailDto) {
        // Validate dependencies
        Order order = orderRepository.findById(orderDetailDto.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

        StoreStock storeStock = storeStockRepository.findById(orderDetailDto.getStoreStockId())
                .orElseThrow(() -> new AppException(ErrorCode.STORE_STOCK_NOT_FOUND));

        // Validate stock availability
        if (!validateStockAvailability(orderDetailDto.getStoreStockId(), orderDetailDto.getQuantity())) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        Promotion promotion = null;
        if (orderDetailDto.getPromotionId() > 0) {
            promotion = promotionRepository.findById(orderDetailDto.getPromotionId()).orElse(null);
        }

        // Calculate total price
        BigDecimal totalPrice = calculateTotalPrice(
                orderDetailDto.getUnitPrice(),
                orderDetailDto.getQuantity(),
                orderDetailDto.getVatAmount(),
                orderDetailDto.getLicensePlateFee(),
                orderDetailDto.getRegistrationFee(),
                orderDetailDto.getDiscountAmount()
        );

        OrderDetail orderDetail = OrderDetail.builder()
                .unitPrice(orderDetailDto.getUnitPrice())
                .quantity(orderDetailDto.getQuantity())
                .vatAmount(orderDetailDto.getVatAmount())
                .licensePlateFee(orderDetailDto.getLicensePlateFee())
                .registrationFee(orderDetailDto.getRegistrationFee())
                .discountAmount(orderDetailDto.getDiscountAmount())
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .order(order)
                .storeStock(storeStock)
                .promotion(promotion)
                .build();

        OrderDetail saved = orderDetailRepository.save(orderDetail);

        // Update stock quantity
        storeStock.setQuantity(storeStock.getQuantity() - orderDetailDto.getQuantity());
        storeStockRepository.save(storeStock);

        return mapToDto(saved);
    }

    @Override
    public OrderDetailDto getOrderDetailById(int id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
        return mapToDto(orderDetail);
    }

    @Override
    public List<OrderDetailDto> getAllOrderDetails() {
        return orderDetailRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public OrderDetailDto updateOrderDetail(int id, OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));

        // Handle quantity change (stock adjustment)
        if (orderDetailDto.getQuantity() != orderDetail.getQuantity()) {
            int oldQuantity = orderDetail.getQuantity();
            int newQuantity = orderDetailDto.getQuantity();
            int stockDifference = oldQuantity - newQuantity;

            // Update stock
            StoreStock storeStock = orderDetail.getStoreStock();
            storeStock.setQuantity(storeStock.getQuantity() + stockDifference);
            storeStockRepository.save(storeStock);
        }

        // Update fields
        orderDetail.setUnitPrice(orderDetailDto.getUnitPrice());
        orderDetail.setQuantity(orderDetailDto.getQuantity());
        orderDetail.setVatAmount(orderDetailDto.getVatAmount());
        orderDetail.setLicensePlateFee(orderDetailDto.getLicensePlateFee());
        orderDetail.setRegistrationFee(orderDetailDto.getRegistrationFee());
        orderDetail.setDiscountAmount(orderDetailDto.getDiscountAmount());

        // Recalculate total price
        BigDecimal newTotalPrice = calculateTotalPrice(
                orderDetailDto.getUnitPrice(),
                orderDetailDto.getQuantity(),
                orderDetailDto.getVatAmount(),
                orderDetailDto.getLicensePlateFee(),
                orderDetailDto.getRegistrationFee(),
                orderDetailDto.getDiscountAmount()
        );
        orderDetail.setTotalPrice(newTotalPrice);
        orderDetail.setUpdatedAt(LocalDateTime.now());

        OrderDetail saved = orderDetailRepository.save(orderDetail);
        return mapToDto(saved);
    }

    @Override
    public void deleteOrderDetail(int id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));

        // Restore stock quantity
        StoreStock storeStock = orderDetail.getStoreStock();
        storeStock.setQuantity(storeStock.getQuantity() + orderDetail.getQuantity());
        storeStockRepository.save(storeStock);

        orderDetailRepository.delete(orderDetail);
    }

    // =============== BUSINESS OPERATIONS ===============

    @Override
    public List<OrderDetailDto> getOrderDetailsByOrderId(int orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public OrderDetailDto updateQuantity(int id, int quantity) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));

        int oldQuantity = orderDetail.getQuantity();
        int stockDifference = oldQuantity - quantity;

        // Check stock availability for increase
        if (quantity > oldQuantity) {
            if (!validateStockAvailability(orderDetail.getStoreStock().getStockId(), quantity - oldQuantity)) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }
        }

        // Update stock
        StoreStock storeStock = orderDetail.getStoreStock();
        storeStock.setQuantity(storeStock.getQuantity() + stockDifference);
        storeStockRepository.save(storeStock);

        // Update order detail
        orderDetail.setQuantity(quantity);

        // Recalculate total price
        BigDecimal newTotalPrice = calculateTotalPrice(
                orderDetail.getUnitPrice(),
                quantity,
                orderDetail.getVatAmount(),
                orderDetail.getLicensePlateFee(),
                orderDetail.getRegistrationFee(),
                orderDetail.getDiscountAmount()
        );
        orderDetail.setTotalPrice(newTotalPrice);
        orderDetail.setUpdatedAt(LocalDateTime.now());

        OrderDetail saved = orderDetailRepository.save(orderDetail);
        return mapToDto(saved);
    }

    // =============== VALIDATION ===============

    @Override
    public boolean validateStockAvailability(int storeStockId, int requestedQuantity) {
        StoreStock storeStock = storeStockRepository.findById(storeStockId).orElse(null);
        if (storeStock == null) {
            return false;
        }
        return storeStock.getQuantity() >= requestedQuantity;
    }

    // =============== CALCULATION ===============

    @Override
    public BigDecimal calculateTotalPrice(BigDecimal unitPrice, int quantity,
                                          BigDecimal vatAmount, BigDecimal licensePlateFee,
                                          BigDecimal registrationFee, BigDecimal discountAmount) {
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal fees = licensePlateFee.add(registrationFee);
        BigDecimal beforeDiscount = subtotal.add(vatAmount).add(fees);
        return beforeDiscount.subtract(discountAmount);
    }

    // =============== HELPER METHODS ===============

    private OrderDetailDto mapToDto(OrderDetail entity) {
        OrderDetailDto dto = OrderDetailDto.builder()
                .id(entity.getId())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .vatAmount(entity.getVatAmount())
                .licensePlateFee(entity.getLicensePlateFee())
                .registrationFee(entity.getRegistrationFee())
                .discountAmount(entity.getDiscountAmount())
                .totalPrice(entity.getTotalPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .orderId(entity.getOrder().getOrderId())
                .storeStockId(entity.getStoreStock().getStockId())
                .promotionId(entity.getPromotion() != null ? entity.getPromotion().getPromotionId() : 0)
                .build();

        // ✅ Set display fields safely - KHÔNG CÓ BRAND:
        if (entity.getStoreStock() != null && entity.getStoreStock().getModelColor() != null) {
            ModelColor modelColor = entity.getStoreStock().getModelColor();

            if (modelColor.getModel() != null) {
                Model model = modelColor.getModel();
                dto.setModelName(model.getModelName()); // ✅ CÓ
                // ❌ LOẠI BỎ: dto.setBrandName(...) - KHÔNG CÓ BRAND!
            }

            if (modelColor.getColor() != null) {
                dto.setColorName(modelColor.getColor().getColorName());
            }

            dto.setModelPrice(entity.getStoreStock().getPriceOfStore());
            dto.setAvailableStock(entity.getStoreStock().getQuantity());
        }

        // Set order info safely
        if (entity.getOrder() != null && entity.getOrder().getStatus() != null) {
            dto.setOrderStatus(entity.getOrder().getStatus().toString());

            if (entity.getOrder().getCustomer() != null) {
                dto.setCustomerName(entity.getOrder().getCustomer().getFullName());
                dto.setCustomerPhone(entity.getOrder().getCustomer().getPhone());
            }
        }

        // Set promotion info safely
        if (entity.getPromotion() != null) {
            dto.setPromotionName(entity.getPromotion().getPromotionName());
            dto.setPromotionType(entity.getPromotion().getPromotionType().toString());
        }

        // Set calculated fields
        dto.setSubtotal(entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity())));
        dto.setTotalFees(entity.getLicensePlateFee().add(entity.getRegistrationFee()));
        dto.setTotalTax(entity.getVatAmount());
        dto.setPriceBeforeDiscount(dto.getSubtotal().add(dto.getTotalFees()).add(dto.getTotalTax()));
        dto.setFinalAmount(entity.getTotalPrice());

        // Set display text
        String modelName = dto.getModelName() != null ? dto.getModelName() : "Unknown Model";
        String colorName = dto.getColorName() != null ? dto.getColorName() : "Unknown Color";
        dto.setDisplayText(String.format("%s - %s (Qty: %d)", modelName, colorName, dto.getQuantity()));

        return dto;
    }
}