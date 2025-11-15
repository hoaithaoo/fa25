package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderDetailsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderWithItemsRequest;
import swp391.fa25.saleElectricVehicle.payload.response.order.*;
import swp391.fa25.saleElectricVehicle.repository.*;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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


//    @Override
//    public StockValidationResponse validateStockAvailability(StockValidationRequest request) {
//        // 1. Validate model exists
//        Model model = modelService.getModelEntityById(request.getModelId());
//
//        // 2. Validate color exists
//        Color color = colorService.getColorEntityById(request.getColorId());
//
//        ModelColor modelColor = modelColorService
//                .getModelColorEntityByModelIdAndColorId(
//                        model.getModelId(),
//                        color.getColorId()
//                );
//
//        // 3. Get store of current user
//        User staff = userService.getCurrentUserEntity();
//        Store store = storeService.getCurrentStoreEntity(staff.getUserId());
//
//        // 4. Get stock from warehouse
//        StoreStock stock = storeStockService
//                .getStoreStockByStoreIdAndModelColorId(
//                        store.getStoreId(),
//                        modelColor.getModelColorId()
//                );
//
//        // 5. Check quantity
//        validateStockAvailability(stock, request.getQuantity());
//
//        // 6. Apply promotion if any (NOT affecting stock validation)
//        if (request.getPromotionId() > 0) {
//            Promotion promotion = promotionService.getPromotionEntityById(request.getPromotionId());
//        }
//
//        // 6. Return success validation (NO DB WRITE)
//        return StockValidationResponse.builder()
//                .modelId(model.getModelId())
//                .modelName(model.getModelName())
//                .colorId(color.getColorId())
//                .colorName(color.getColorName())
//                .requestedQuantity(request.getQuantity())
//                .promotionId(request.getPromotionId())
//                .promotionName(request.getPromotionId() > 0 ?
//                        promotionService.getPromotionEntityById(request.getPromotionId()).getPromotionName() : null)
//                .build();
//                .availableStock(stock.getQuantity())
//                .isAvailable(true)
//    }

// =============== CRUD OPERATIONS ===============

    // xem xét nên chuyển qua order service không
    @Override
    public GetQuoteResponse createQuote(CreateOrderWithItemsRequest request) {
        // Validate dependencies
        Order order = orderService.getOrderEntityById(request.getOrderId());

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE);
        }

        // lấy thông tin của user và store hiện tại
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // default để tính toán tổng
        List<OrderDetail> orderDetails = order.getOrderDetails();
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPromotions = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;

        // Check if customer selected license plate service
        BigDecimal licensePlateFee = BigDecimal.ZERO;
        BigDecimal registrationFee = BigDecimal.ZERO;

        int quantity = 0;

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

//            User staff = userService.getCurrentUserEntity();
//            Store store = storeService.getCurrentStoreEntity(staff.getUserId());

            StoreStock stock = storeStockService.getStoreStockByStoreIdAndModelColorId(
                    store.getStoreId(),
                    modelColor.getModelColorId()
            );

            // ✅ Validate stock availability (available = quantity - reservedQuantity)
            int availableStock = stock.getQuantity() - stock.getReservedQuantity();
            validateStockAvailability(stock, itemReq.getQuantity(), availableStock);

            // tính toán tổng số lượng xe trong đơn
            quantity += itemReq.getQuantity();

            // đơn giá bao gồm cả VAT
            BigDecimal unitPriceAfterVat = stock.getPriceOfStore()
                    .add(stock.getPriceOfStore().multiply(BigDecimal.valueOf(VAT_AMOUNT_RATE)));
            // tổng tiền xe (đã gồm vat)
            totalOrderPrice = totalOrderPrice.add(unitPriceAfterVat.multiply(BigDecimal.valueOf(itemReq.getQuantity())));


            if (request.isIncludeLicensePlateService()) {
                // Khách chọn dịch vụ đăng ký biển số
                licensePlateFee = LICENSE_PLATE_AMOUNT_200K.multiply(BigDecimal.valueOf(itemReq.getQuantity())); // Default 200K/vehicle
                if (store.getProvinceName().equalsIgnoreCase("Thành phố Hồ Chí Minh")
                        || store.getProvinceName().equalsIgnoreCase("Thành phố Hà Nội")) {
                    licensePlateFee = (LICENSE_PLATE_AMOUNT_20M).multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                }
                registrationFee = REGISTRATION_FEE_AMOUNT.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            }
            // Nếu không chọn dịch vụ thì licensePlateFee và registrationFee đã = 0

            totalTax = totalTax.add(licensePlateFee).add(registrationFee);

            // Calculate discount amount
            Promotion promotion = null;
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (itemReq.getPromotionId() > 0) {
                // Chỉ lấy promotion của đại lý (không phải hãng) và còn active
                promotion = promotionService.getStorePromotionEntityById(itemReq.getPromotionId());
                
                // Giả sử promotion là giảm giá theo phần trăm
                if (promotion.getPromotionType().toString().equals("PERCENTAGE")) {
                    discountAmount = stock.getPriceOfStore()
                            .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                            .multiply(promotion.getAmount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                } else if (promotion.getPromotionType().toString().equals("FIXED_AMOUNT")) {
                    discountAmount = promotion.getAmount().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                }
            }

            // tổng tiền khuyến mãi
            totalPromotions = totalPromotions.add(discountAmount);

            // Calculate total price
            // tính vat ở trong hàm calculateTotalPrice
            price = calculateTotalPrice(
                    stock.getPriceOfStore(),
                    itemReq.getQuantity(),
//                    vatAmount,
                    licensePlateFee,
                    registrationFee,
                    discountAmount
            );
            // calculate final amount
            finalAmount = finalAmount.add(price);

            // Create OrderDetail
            OrderDetail orderDetail = OrderDetail.builder()
                    // set đơn giá là giá tại cửa hàng đã bao gồm VAT
                    .unitPrice(unitPriceAfterVat)
                    .quantity(itemReq.getQuantity())
                    .licensePlateFee(licensePlateFee) // phí biển số theo số lượng
                    .registrationFee(registrationFee)
                    .discountAmount(discountAmount)
                    .totalPrice(price) // tiền một model sau phí dịch vụ và khuyến mãi
                    .createdAt(LocalDateTime.now())
                    .order(order)
                    .storeStock(stock)
                    .promotion(promotion)
                    .build();

            // Save OrderDetail
            OrderDetail saved = orderDetailRepository.save(orderDetail);
            orderDetails.add(saved);
        }

//        if (request.isIncludeLicensePlateService()) {
//            // Khách chọn dịch vụ đăng ký biển số
//            licensePlateFee = LICENSE_PLATE_AMOUNT_200K.multiply(BigDecimal.valueOf(quantity)); // Default 200K/vehicle * tổng số xe
//            if (store.getProvinceName().equalsIgnoreCase("Thành phố Hồ Chí Minh")
//                    || store.getProvinceName().equalsIgnoreCase("Thành phố Hà Nội")) {
//                licensePlateFee = (LICENSE_PLATE_AMOUNT_20M).multiply(BigDecimal.valueOf(quantity)); // 20M/vehicle * tổng số xe
//            }
//            registrationFee = REGISTRATION_FEE_AMOUNT.multiply(BigDecimal.valueOf(quantity));
//        }
//        // Nếu không chọn dịch vụ thì licensePlateFee và registrationFee đã = 0
//
//        totalTax = totalTax.add(licensePlateFee).add(registrationFee);

        // cập nhật order khi có order details
        order.setTotalPrice(totalOrderPrice);
        order.setTotalTaxPrice(totalTax);
        order.setTotalPromotionAmount(totalPromotions);
        order.setTotalPayment(finalAmount);
        orderService.updateOrder(order);

        return GetQuoteResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .getOrderDetailsResponses(
                        orderDetails.stream().map(od -> GetOrderDetailsResponse.builder()
                                .orderDetailId(od.getId())
                                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                                .unitPrice(od.getUnitPrice())
                                .quantity(od.getQuantity())
                                .licensePlateFee(od.getLicensePlateFee())
                                .registrationFee(od.getRegistrationFee())
                                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : 0)
                                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                                .discountAmount(od.getDiscountAmount())
                                .totalPrice(od.getTotalPrice())
                                .build()
                        ).toList())
                .totalPrice(order.getTotalPrice())
                .totalLicensePlateFee(licensePlateFee)
                .totalRegistrationFee(registrationFee)
                .totalPromotionAmount(order.getTotalPromotionAmount())
                .totalPayment(order.getTotalPayment())
                .status(order.getStatus().name())
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

    @Override
    public List<GetOrderDetailsResponse> getOrderDetailsByOrderId(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderIdAndOrder_Store_StoreId(orderId, currentStore.getStoreId());
        return orderDetails.stream().map(this::mapToDto).toList();
    }
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

    private void validateStockAvailability(StoreStock stock, int requestedQuantity, int availableStock) {
        if (availableStock < requestedQuantity) {
            // Throw exception với thông tin chi tiết
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, String.format(
                    ". Sản phẩm %s không đủ hàng. Còn %d, yêu cầu %d",
                    stock.getModelColor().getModel().getModelName(),
                    availableStock,
                    requestedQuantity));
        }
    }

//      Override
    private BigDecimal calculateTotalPrice(BigDecimal priceOfStore, int quantity,
//                                          BigDecimal vatAmount,
                                           BigDecimal licensePlateFee,
                                           BigDecimal registrationFee, BigDecimal discountAmount) {
        BigDecimal totalPrice = priceOfStore.add(priceOfStore.multiply(BigDecimal.valueOf(VAT_AMOUNT_RATE)))
                .multiply(BigDecimal.valueOf(quantity))
//                .add(vatAmount)
                .add(licensePlateFee)
                .add(registrationFee)
                .subtract(discountAmount);
        
        // Đảm bảo totalPrice không bao giờ âm (nếu âm thì set = 0)
        return totalPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalPrice;
    }

    private GetOrderDetailsResponse mapToDto(OrderDetail od) {
        return GetOrderDetailsResponse.builder()
                .orderDetailId(od.getId())
                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                .unitPrice(od.getUnitPrice())
                .quantity(od.getQuantity())
//                .vatAmount(od.getVatAmount())
                .licensePlateFee(od.getLicensePlateFee())
                .registrationFee(od.getRegistrationFee())
                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : null)
                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                .discountAmount(od.getDiscountAmount())
                .totalPrice(od.getTotalPrice())
                .build();
    }
}