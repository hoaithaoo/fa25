package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.VehicleDto;
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
    @Lazy
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

    private final BigDecimal LICENSE_PLATE_FEE_HCM_HN = BigDecimal.valueOf(10000000); // 10 millions VND - Hà Nội và TP. Hồ Chí Minh
    private final BigDecimal LICENSE_PLATE_FEE_OTHER = BigDecimal.valueOf(1000000); // 1 million VND - các tỉnh thành khác
    private final BigDecimal SERVICE_FEE_AMOUNT = BigDecimal.valueOf(1500000); // 1.5 millions VND - phí đăng ký biển số (cố định)
    private final BigDecimal OTHER_TAX_AMOUNT = BigDecimal.valueOf(2500000); // 2.5 millions VND - các loại thuế khác (hardcode)

// =============== CRUD OPERATIONS ===============

    // xem xét nên chuyển qua order service không
    @Override
    public GetQuoteResponse createQuote(CreateOrderWithItemsRequest request) {
        // Validate dependencies
        Order order = orderService.getOrderEntityById(request.getOrderId());

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE);
        }

        // Validation: Không cho phép tạo quote nếu order đã có order details
        if (!order.getOrderDetails().isEmpty()) {
            throw new AppException(ErrorCode.ORDER_ALREADY_HAS_DETAILS);
        }

        // lấy thông tin của user và store hiện tại
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // Validation: Order phải thuộc store của user hiện tại
        if (order.getStore() == null || order.getStore().getStoreId() != store.getStoreId()) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        // default để tính toán tổng
        List<OrderDetail> orderDetails = order.getOrderDetails();
        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPromotions = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;

        // Tổng phí biển số và các loại phí khác cho tất cả items
        BigDecimal totalLicensePlateFee = BigDecimal.ZERO;
        BigDecimal totalServiceFee = BigDecimal.ZERO;
        BigDecimal totalOtherTax = BigDecimal.ZERO;

        // Không merge nữa: xử lý từng item riêng biệt, mỗi item tạo 1 detail
        for (CreateOrderDetailsRequest itemReq : request.getOrderDetails()) {
            // Validation: quantity phải > 0
            if (itemReq.getQuantity() <= 0) {
                throw new AppException(ErrorCode.INVALID_NUMBER, "Số lượng phải lớn hơn 0");
            }

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

            // kiểm tra xem còn đủ xe có sẵn không (available = quantity - reservedQuantity)
            int availableStock = stock.getQuantity() - stock.getReservedQuantity();
            validateStockAvailability(stock, itemReq.getQuantity(), availableStock);

            // đơn giá KHÔNG bao gồm VAT (đã bỏ VAT)
            BigDecimal unitPrice = stock.getPriceOfStore();
            // tổng tiền xe (không gồm vat) = đơn giá * số lượng
            totalOrderPrice = totalOrderPrice.add(unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            // Tính phí biển số và các loại phí khác cho từng item (nhân với quantity)
            BigDecimal itemLicensePlateFee = BigDecimal.ZERO;
            BigDecimal itemServiceFee = BigDecimal.ZERO;
            BigDecimal itemOtherTax = BigDecimal.ZERO;
            
            if (itemReq.isIncludeLicensePlateService()) {
                // Khách chọn dịch vụ đăng ký biển số
                // Phí biển số: 10tr ở Hà Nội và TP. Hồ Chí Minh, 1tr ở nơi khác
                if (store.getProvinceName().equalsIgnoreCase("Thành phố Hồ Chí Minh")
                        || store.getProvinceName().equalsIgnoreCase("Thành phố Hà Nội")) {
                    itemLicensePlateFee = LICENSE_PLATE_FEE_HCM_HN;
                } else {
                    itemLicensePlateFee = LICENSE_PLATE_FEE_OTHER;
                }
                
                // Phí đăng ký biển số (serviceFee): 1.5tr (cố định) * quantity
                itemServiceFee = SERVICE_FEE_AMOUNT.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                
                // Other tax: 2.5tr (hardcode) * quantity
                itemOtherTax = OTHER_TAX_AMOUNT.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                
                // Cộng dồn vào tổng
                totalLicensePlateFee = totalLicensePlateFee.add(itemLicensePlateFee);
                totalServiceFee = totalServiceFee.add(itemServiceFee);
                totalOtherTax = totalOtherTax.add(itemOtherTax);
            }
            // Nếu khách không muốn làm giấy tờ (includeLicensePlateService = false) thì các loại phí khác = 0

            // Các loại phí khác = phí đăng ký biển số (serviceFee) + other tax
            BigDecimal itemOtherFees = itemServiceFee.add(itemOtherTax);
            totalTax = totalTax.add(itemLicensePlateFee).add(itemOtherFees);

            // Calculate discount amount
            Promotion promotion = null;
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (itemReq.getPromotionId() != null && itemReq.getPromotionId() > 0) {
                // Chỉ lấy promotion của đại lý (không phải hãng) và còn active
                promotion = promotionService.getStorePromotionEntityById(itemReq.getPromotionId());
                
                // Validation: Promotion phải thuộc model của item
                if (promotion.getModel() == null || promotion.getModel().getModelId() != model.getModelId()) {
                    throw new AppException(ErrorCode.PROMOTION_NOT_EXIST, 
                        String.format("Khuyến mãi không áp dụng cho model %s", model.getModelName()));
                }
                
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

            // Calculate total price cho toàn bộ detail (tất cả vehicles)
            BigDecimal price = calculateTotalPrice(
                    stock.getPriceOfStore(),
                    itemReq.getQuantity(),
                    itemLicensePlateFee,
                    itemServiceFee,
                    itemOtherTax,
                    discountAmount
            );
            // calculate final amount
            finalAmount = finalAmount.add(price);

            // Create OrderDetail - 1 detail cho model+color, có quantity
            // báo giá chưa có xe cụ thể nên chưa gán được vehicle
            OrderDetail orderDetail = OrderDetail.builder()
                    // set đơn giá là giá tại cửa hàng KHÔNG bao gồm VAT
                    .unitPrice(unitPrice)
                    .quantity(itemReq.getQuantity()) // số lượng xe trong detail này
                    .licensePlateFee(itemLicensePlateFee) // phí biển số cho tất cả xe
                    .serviceFee(itemServiceFee) // phí đăng ký biển số cho tất cả xe
                    .otherTax(itemOtherTax) // các loại thuế khác cho tất cả xe
                    .discountAmount(discountAmount)
                    .totalPrice(price) // tổng tiền cho tất cả xe trong detail
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

        // Đảm bảo finalAmount không bao giờ âm (nếu âm thì set = 0)
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        
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
                                .quantity(od.getQuantity()) // Số lượng vehicle trong detail
                                .licensePlateFee(od.getLicensePlateFee()) // tiền biển số
                                .serviceFee(od.getServiceFee()) // tiền đăng kí biển số
                                .otherTax(od.getOtherTax()) // tiền các loại thuế khác
                                .otherFees(od.getServiceFee().add(od.getOtherTax())) // phí khác (gồm phí đăng ký biển số + thuế khác)
                                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : 0)
                                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                                .discountAmount(od.getDiscountAmount())
                                .totalPrice(od.getTotalPrice())
                                .build()
                        ).toList())
                .totalPrice(order.getTotalPrice())
                .totalLicensePlateFee(totalLicensePlateFee) // tổng phí biển số
                .totalServiceFee(totalServiceFee) // tổng phí đăng kí biển số
                .totalOtherTax(totalOtherTax) // tổng thuế khác
                .totalOtherFees(totalServiceFee.add(totalOtherTax)) // tổng phí khác (gồm phí đăng ký biển số + thuế khác)
                .totalPromotionAmount(order.getTotalPromotionAmount())
                .totalPayment(order.getTotalPayment())
                .status(order.getStatus().name())
                .build();
    }

    @Override
    public GetQuoteResponse updateQuote(CreateOrderWithItemsRequest request) {
        // Validate dependencies
        Order order = orderService.getOrderEntityById(request.getOrderId());

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new AppException(ErrorCode.ORDER_NOT_EDITABLE, 
                "Chỉ có thể sửa báo giá cho đơn hàng ở trạng thái DRAFT");
        }

        // lấy thông tin của user và store hiện tại
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // Validation: Order phải thuộc store của user hiện tại
        if (order.getStore() == null || order.getStore().getStoreId() != store.getStoreId()) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        // Không merge nữa: xử lý từng item riêng biệt, mỗi item tạo 1 detail
        // Xóa tất cả existing order details và tạo lại từ đầu
        List<OrderDetail> existingOrderDetails = new java.util.ArrayList<>(order.getOrderDetails());
        for (OrderDetail existingDetail : existingOrderDetails) {
            orderDetailRepository.delete(existingDetail);
        }
        order.getOrderDetails().clear();
        
        // default để tính toán tổng
        List<OrderDetail> orderDetails = new java.util.ArrayList<>();
        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPromotions = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;

        // Tổng phí biển số và các loại phí khác cho tất cả items
        BigDecimal totalLicensePlateFee = BigDecimal.ZERO;
        BigDecimal totalServiceFee = BigDecimal.ZERO;
        BigDecimal totalOtherTax = BigDecimal.ZERO;
        
        // Xử lý từng item trong request (không merge, mỗi item tạo 1 detail riêng)
        for (CreateOrderDetailsRequest itemReq : request.getOrderDetails()) {
            // Validation: quantity phải > 0
            if (itemReq.getQuantity() <= 0) {
                throw new AppException(ErrorCode.INVALID_NUMBER);
            }

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

            StoreStock stock = storeStockService.getStoreStockByStoreIdAndModelColorId(
                    store.getStoreId(),
                    modelColor.getModelColorId()
            );

            // Validate stock availability (available = quantity - reservedQuantity)
            int availableStock = stock.getQuantity() - stock.getReservedQuantity();
            validateStockAvailability(stock, itemReq.getQuantity(), availableStock);

            // đơn giá KHÔNG bao gồm VAT (đã bỏ VAT)
            BigDecimal unitPrice = stock.getPriceOfStore();
            // tổng tiền xe (không gồm vat) = đơn giá * số lượng
            totalOrderPrice = totalOrderPrice.add(unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            // Tính phí biển số và các loại phí khác cho từng item (nhân với quantity)
            BigDecimal itemLicensePlateFee = BigDecimal.ZERO;
            BigDecimal itemServiceFee = BigDecimal.ZERO;
            BigDecimal itemOtherTax = BigDecimal.ZERO;
            
            if (itemReq.isIncludeLicensePlateService()) {
                // Khách chọn dịch vụ đăng ký biển số
                // Phí biển số: 10tr ở Hà Nội và TP. Hồ Chí Minh, 1tr ở nơi khác
                if (store.getProvinceName().equalsIgnoreCase("Thành phố Hồ Chí Minh")
                        || store.getProvinceName().equalsIgnoreCase("Thành phố Hà Nội")) {
                    itemLicensePlateFee = LICENSE_PLATE_FEE_HCM_HN;
                } else {
                    itemLicensePlateFee = LICENSE_PLATE_FEE_OTHER;
                }
                
                // Phí đăng ký biển số (serviceFee): 1.5tr (cố định) * quantity
                itemServiceFee = SERVICE_FEE_AMOUNT.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                
                // Other tax: 2.5tr (hardcode) * quantity
                itemOtherTax = OTHER_TAX_AMOUNT.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                
                // Cộng dồn vào tổng
                totalLicensePlateFee = totalLicensePlateFee.add(itemLicensePlateFee);
                totalServiceFee = totalServiceFee.add(itemServiceFee);
                totalOtherTax = totalOtherTax.add(itemOtherTax);
            }
            // Nếu khách không muốn làm giấy tờ (includeLicensePlateService = false) thì các loại phí khác = 0

            // Các loại phí khác = phí đăng ký biển số (serviceFee) + other tax
            BigDecimal itemOtherFees = itemServiceFee.add(itemOtherTax);
            totalTax = totalTax.add(itemLicensePlateFee).add(itemOtherFees);

            // Calculate discount amount
            Promotion promotion = null;
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (itemReq.getPromotionId() != null && itemReq.getPromotionId() > 0) {
                // Chỉ lấy promotion của đại lý (không phải hãng) và còn active
                promotion = promotionService.getStorePromotionEntityById(itemReq.getPromotionId());
                
                // Validation: Promotion phải thuộc model của item
                if (promotion.getModel() == null || promotion.getModel().getModelId() != model.getModelId()) {
                    throw new AppException(ErrorCode.PROMOTION_NOT_EXIST, 
                        String.format("Khuyến mãi không áp dụng cho model %s", model.getModelName()));
                }
                
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

            // Calculate total price cho toàn bộ detail (tất cả vehicles)
            BigDecimal price = calculateTotalPrice(
                    stock.getPriceOfStore(),
                    itemReq.getQuantity(),
                    itemLicensePlateFee,
                    itemServiceFee,
                    itemOtherTax,
                    discountAmount
            );
            // calculate final amount
            finalAmount = finalAmount.add(price);

            // Create new OrderDetail - mỗi item tạo 1 detail riêng (không merge)
            OrderDetail orderDetail = OrderDetail.builder()
                    // set đơn giá là giá tại cửa hàng KHÔNG bao gồm VAT
                    .unitPrice(unitPrice)
                    .quantity(itemReq.getQuantity()) // số lượng xe trong detail này
                    .licensePlateFee(itemLicensePlateFee) // phí biển số cho tất cả xe
                    .serviceFee(itemServiceFee) // phí đăng ký biển số cho tất cả xe
                    .otherTax(itemOtherTax) // các loại thuế khác cho tất cả xe
                    .discountAmount(discountAmount)
                    .totalPrice(price) // tổng tiền cho tất cả xe trong detail
                    .createdAt(LocalDateTime.now())
                    .order(order)
                    .storeStock(stock)
                    .promotion(promotion)
                    .build();
            OrderDetail saved = orderDetailRepository.save(orderDetail);
            orderDetails.add(saved);
        }

        // Đảm bảo finalAmount không bao giờ âm (nếu âm thì set = 0)
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        
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
                        orderDetails.stream().map(this::mapToDto).toList())
                .totalPrice(order.getTotalPrice())
                .totalLicensePlateFee(totalLicensePlateFee) // tổng phí biển số
                .totalServiceFee(totalServiceFee) // tổng phí đăng kí biển số
                .totalOtherTax(totalOtherTax) // tổng thuế khác
                .totalOtherFees(totalServiceFee.add(totalOtherTax)) // tổng phí khác (gồm phí đăng ký biển số + thuế khác)
                .totalPromotionAmount(order.getTotalPromotionAmount())
                .totalPayment(order.getTotalPayment())
                .status(order.getStatus().name())
                .build();
    }

    // Merge duplicate items trong request (cùng model + color) bằng cách cộng dồn quantity
    // DEPRECATED: Không còn sử dụng nữa vì không merge nữa
    // @Deprecated
    // private Map<String, CreateOrderDetailsRequest> mergeDuplicateItem(CreateOrderWithItemsRequest request) {
    //     Map<String, CreateOrderDetailsRequest> mergedRequestItems = new HashMap<>();

    //     for (int i = 0; i < request.getOrderDetails().size(); i++) {
    //         CreateOrderDetailsRequest itemReq = request.getOrderDetails().get(i);

    //         // Validation: quantity phải > 0
    //         if (itemReq.getQuantity() <= 0) {
    //             throw new AppException(ErrorCode.INVALID_NUMBER, "Số lượng phải lớn hơn 0");
    //         }

    //         // Merge duplicate items: nếu đã có item với cùng model+color thì cộng dồn quantity
    //         String itemKey = itemReq.getModelId() + "_" + itemReq.getColorId();
    //         if (mergedRequestItems.containsKey(itemKey)) {
    //             CreateOrderDetailsRequest existingItem = mergedRequestItems.get(itemKey);
    //             // Cộng dồn quantity, giữ promotionId của item đầu tiên
    //             existingItem.setQuantity(existingItem.getQuantity() + itemReq.getQuantity());
    //         } else {
    //             mergedRequestItems.put(itemKey, itemReq);
    //         }
    //     }
    //     return mergedRequestItems;
    // }


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

    private BigDecimal calculateTotalPrice(BigDecimal priceOfStore, int quantity,
                                           BigDecimal licensePlateFee,
                                           BigDecimal serviceFee,
                                           BigDecimal otherTax,
                                           BigDecimal discountAmount) {
        // Đơn giá KHÔNG bao gồm VAT
        // 1 detail có thể có nhiều vehicle (quantity)
        // Các loại phí khác = serviceFee + otherTax
        BigDecimal otherFees = serviceFee.add(otherTax);
        // Tính tổng: (đơn giá * số lượng) + phí biển số + phí khác - khuyến mãi
        BigDecimal totalPrice = priceOfStore
                .multiply(BigDecimal.valueOf(quantity))
                .add(licensePlateFee)
                .add(otherFees)
                .subtract(discountAmount);
        
        // Đảm bảo totalPrice không bao giờ âm (nếu âm thì set = 0)
        return totalPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalPrice;
    }

    private GetOrderDetailsResponse mapToDto(OrderDetail od) {
        // Map list vehicles từ order detail sang VehicleSimpleResponse (chỉ thông tin cơ bản)
        List<swp391.fa25.saleElectricVehicle.payload.response.order.VehicleSimpleResponse> vehicles = null;
        if (od.getVehicles() != null && !od.getVehicles().isEmpty()) {
            vehicles = od.getVehicles().stream()
                    .map(v -> swp391.fa25.saleElectricVehicle.payload.response.order.VehicleSimpleResponse.builder()
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
    
    /**
     * Map Vehicle entity sang VehicleDto
     */
    private VehicleDto mapToVehicleDto(Vehicle vehicle) {
        return VehicleDto.builder()
                .vehicleId(vehicle.getVehicleId())
                .vin(vehicle.getVin())
                .engineNo(vehicle.getEngineNo())
                .batteryNo(vehicle.getBatteryNo())
                .status(vehicle.getStatus() != null ? vehicle.getStatus().name() : null)
                .importDate(vehicle.getImportDate())
                .saleDate(vehicle.getSaleDate())
                .notes(vehicle.getNotes())
                .inventoryTransaction(vehicle.getInventoryTransaction() != null ?
                        vehicle.getInventoryTransaction().getInventoryId() : 0)
                .build();
    }

    @Override
    public void updateOrderDetail(swp391.fa25.saleElectricVehicle.entity.OrderDetail orderDetail) {
        orderDetailRepository.save(orderDetail);
    }

    @Override
    public swp391.fa25.saleElectricVehicle.entity.OrderDetail getOrderDetailEntityById(int orderDetailId) {
        return orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
    }

    /**
     * Helper method để map list vehicles từ order detail (public để OrderServiceImpl có thể dùng)
     */
//    public List<VehicleDto> mapVehiclesToList(OrderDetail od) {
//        if (od.getVehicles() == null || od.getVehicles().isEmpty()) {
//            return new java.util.ArrayList<>();
//        }
//        return od.getVehicles().stream()
//                .map(this::mapToVehicleDto)
//                .toList();
//    }
}