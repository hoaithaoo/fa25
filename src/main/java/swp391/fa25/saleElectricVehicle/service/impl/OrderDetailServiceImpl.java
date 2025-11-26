package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderDetailsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.order.CreateOrderWithItemsRequest;
import swp391.fa25.saleElectricVehicle.payload.request.order.VehicleAssignment;
import swp391.fa25.saleElectricVehicle.payload.response.order.*;
import swp391.fa25.saleElectricVehicle.repository.*;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private VehicleService vehicleService;

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

        // Merge duplicate items (cùng model + color) bằng cách cộng dồn quantity
        Map<String, CreateOrderDetailsRequest> mergedItems = mergeDuplicateItem(request);

        // Xử lý các items đã được merge
        for (CreateOrderDetailsRequest itemReq : mergedItems.values()) {

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
            
            // Calculate promotion (tính cho 1 xe)
            Promotion promotion = null;
            BigDecimal discountPerVehicle = BigDecimal.ZERO;
            if (itemReq.getPromotionId() != null && itemReq.getPromotionId() > 0) {
                // Chỉ lấy promotion của đại lý (không phải hãng) và còn active
                promotion = promotionService.getStorePromotionEntityById(itemReq.getPromotionId());
                
                // Validation: Promotion phải thuộc model của item
                if (promotion.getModel() == null || promotion.getModel().getModelId() != model.getModelId()) {
                    throw new AppException(ErrorCode.PROMOTION_NOT_EXIST, 
                        String.format("Khuyến mãi không áp dụng cho model %s", model.getModelName()));
                }
                
                // Tính discount cho 1 xe
                if (promotion.getPromotionType().toString().equals("PERCENTAGE")) {
                    discountPerVehicle = stock.getPriceOfStore()
                            .multiply(promotion.getAmount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                } else if (promotion.getPromotionType().toString().equals("FIXED_AMOUNT")) {
                    discountPerVehicle = promotion.getAmount();
                }
            }
            
            // Tạo nhiều detail, mỗi detail = 1 vehicle
            // Mỗi detail tương ứng với 1 vehicle, không có quantity
            for (int i = 0; i < itemReq.getQuantity(); i++) {
                // Tính phí cho 1 xe
                BigDecimal itemLicensePlateFee = BigDecimal.ZERO;
                BigDecimal itemServiceFee = BigDecimal.ZERO;
                BigDecimal itemOtherTax = BigDecimal.ZERO;
                
                if (request.isIncludeLicensePlateService()) {
                    // Khách chọn dịch vụ đăng ký biển số
                    // Phí biển số: 10tr ở Hà Nội và TP. Hồ Chí Minh, 1tr ở nơi khác
                    if (store.getProvinceName().equalsIgnoreCase("Thành phố Hồ Chí Minh")
                            || store.getProvinceName().equalsIgnoreCase("Thành phố Hà Nội")) {
                        itemLicensePlateFee = LICENSE_PLATE_FEE_HCM_HN;
                    } else {
                        itemLicensePlateFee = LICENSE_PLATE_FEE_OTHER;
                    }
                    
                    // Phí đăng ký biển số (serviceFee): 1.5tr (cố định)
                    itemServiceFee = SERVICE_FEE_AMOUNT;
                    
                    // Other tax: 2.5tr (hardcode)
                    itemOtherTax = OTHER_TAX_AMOUNT;
                    
                    // Cộng dồn vào tổng
                    totalLicensePlateFee = totalLicensePlateFee.add(itemLicensePlateFee);
                    totalServiceFee = totalServiceFee.add(itemServiceFee);
                    totalOtherTax = totalOtherTax.add(itemOtherTax);
                }
                
                // Các loại phí khác = phí đăng ký biển số (serviceFee) + other tax
                BigDecimal itemOtherFees = itemServiceFee.add(itemOtherTax);
                totalTax = totalTax.add(itemLicensePlateFee).add(itemOtherFees);
                
                // Tính total price cho 1 xe
                BigDecimal priceForOneVehicle = calculateTotalPrice(
                        stock.getPriceOfStore(),
                        1, // Mỗi detail = 1 vehicle
                        itemLicensePlateFee,
                        itemServiceFee,
                        itemOtherTax,
                        discountPerVehicle
                );
                
                // Cộng vào tổng
                totalOrderPrice = totalOrderPrice.add(unitPrice);
                totalPromotions = totalPromotions.add(discountPerVehicle);
                finalAmount = finalAmount.add(priceForOneVehicle);
                
                // Create OrderDetail - mỗi detail = 1 vehicle, không có quantity field
                // báo giá chưa có xe cụ thể nên chưa gán được vehicle
                OrderDetail orderDetail = OrderDetail.builder()
                        // set đơn giá là giá tại cửa hàng KHÔNG bao gồm VAT
                        .unitPrice(unitPrice)
                        // quantity đã bị xóa, mỗi detail = 1 vehicle
                        .licensePlateFee(itemLicensePlateFee) // phí biển số cho 1 xe
                        .serviceFee(itemServiceFee) // phí đăng ký biển số cho 1 xe
                        .otherTax(itemOtherTax) // các loại thuế khác cho 1 xe
                        .discountAmount(discountPerVehicle)
                        .totalPrice(priceForOneVehicle) // tiền cho 1 xe
                        .createdAt(LocalDateTime.now())
                        .order(order)
                        .storeStock(stock)
                        .promotion(promotion)
                        .build();
                
                // Save OrderDetail
                OrderDetail saved = orderDetailRepository.save(orderDetail);
                orderDetails.add(saved);
            }
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
                                .quantity(1) // Mỗi detail = 1 vehicle
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

        // Tối ưu: So sánh và update/delete/create từng item thay vì xóa hết rồi tạo lại
        List<OrderDetail> existingOrderDetails = new java.util.ArrayList<>(order.getOrderDetails());
        
        // Tạo Map từ existing order details theo key modelId_colorId để dễ tìm
        // Map này lưu List vì có thể có nhiều detail với cùng model+color (mỗi detail = 1 vehicle)
        Map<String, List<OrderDetail>> existingDetailsMap = new HashMap<>();
        for (OrderDetail existingDetail : existingOrderDetails) {
            int modelId = existingDetail.getStoreStock().getModelColor().getModel().getModelId();
            int colorId = existingDetail.getStoreStock().getModelColor().getColor().getColorId();
            String key = modelId + "_" + colorId;
            existingDetailsMap.computeIfAbsent(key, k -> new ArrayList<>()).add(existingDetail);
        }

        // Merge duplicate items trong request (cùng model + color)
        Map<String, CreateOrderDetailsRequest> mergedRequestItems = mergeDuplicateItem(request);
//        Map<String, CreateOrderDetailsRequest> mergedRequestItems;

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
        
        // Xử lý các items đã được merge từ request
        for (CreateOrderDetailsRequest itemReq : mergedRequestItems.values()) {
            String itemKey = itemReq.getModelId() + "_" + itemReq.getColorId();
            List<OrderDetail> existingDetailsForModelColor = existingDetailsMap.get(itemKey);

            // Kiểm tra: Nếu model+color và số lượng detail giống với quantity mới thì giữ nguyên
            if (existingDetailsForModelColor != null && existingDetailsForModelColor.size() == itemReq.getQuantity()) {
                // Giữ nguyên order details cũ, chỉ tính lại vào tổng
                for (OrderDetail existingDetail : existingDetailsForModelColor) {
                    // Mỗi detail = 1 vehicle, không nhân với quantity
                    totalOrderPrice = totalOrderPrice.add(existingDetail.getUnitPrice());
                    BigDecimal itemOtherFees = existingDetail.getServiceFee().add(existingDetail.getOtherTax());
                    totalTax = totalTax.add(existingDetail.getLicensePlateFee()).add(itemOtherFees);
                    totalPromotions = totalPromotions.add(existingDetail.getDiscountAmount());
                    finalAmount = finalAmount.add(existingDetail.getTotalPrice());
                    totalLicensePlateFee = totalLicensePlateFee.add(existingDetail.getLicensePlateFee());
                    totalServiceFee = totalServiceFee.add(existingDetail.getServiceFee());
                    totalOtherTax = totalOtherTax.add(existingDetail.getOtherTax());
                }
                
                // Đánh dấu đã xử lý để không xóa sau này
                existingDetailsMap.remove(itemKey);
                continue; // Skip phần còn lại, không update
            }
            
            // Xóa tất cả existing details của model+color này (sẽ tạo lại)
            if (existingDetailsForModelColor != null) {
                for (OrderDetail detailToDelete : existingDetailsForModelColor) {
                    orderDetailRepository.delete(detailToDelete);
                    orderDetails.remove(detailToDelete);
                }
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
            
            // Calculate promotion (tính cho 1 xe)
            Promotion promotion = null;
            BigDecimal discountPerVehicle = BigDecimal.ZERO;
            if (itemReq.getPromotionId() != null && itemReq.getPromotionId() > 0) {
                // Chỉ lấy promotion của đại lý (không phải hãng) và còn active
                promotion = promotionService.getStorePromotionEntityById(itemReq.getPromotionId());
                
                // Validation: Promotion phải thuộc model của item
                if (promotion.getModel() == null || promotion.getModel().getModelId() != model.getModelId()) {
                    throw new AppException(ErrorCode.PROMOTION_NOT_EXIST, 
                        String.format("Khuyến mãi không áp dụng cho model %s", model.getModelName()));
                }
                
                // Tính discount cho 1 xe
                if (promotion.getPromotionType().toString().equals("PERCENTAGE")) {
                    discountPerVehicle = stock.getPriceOfStore()
                            .multiply(promotion.getAmount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                } else if (promotion.getPromotionType().toString().equals("FIXED_AMOUNT")) {
                    discountPerVehicle = promotion.getAmount();
                }
            }
            
            // Tạo nhiều detail, mỗi detail = 1 vehicle
            // Mỗi detail tương ứng với 1 vehicle, không có quantity
            for (int i = 0; i < itemReq.getQuantity(); i++) {
                // Tính phí cho 1 xe
                BigDecimal itemLicensePlateFee = BigDecimal.ZERO;
                BigDecimal itemServiceFee = BigDecimal.ZERO;
                BigDecimal itemOtherTax = BigDecimal.ZERO;
                
                if (request.isIncludeLicensePlateService()) {
                    // Khách chọn dịch vụ đăng ký biển số
                    // Phí biển số: 10tr ở Hà Nội và TP. Hồ Chí Minh, 1tr ở nơi khác
                    if (store.getProvinceName().equalsIgnoreCase("Thành phố Hồ Chí Minh")
                            || store.getProvinceName().equalsIgnoreCase("Thành phố Hà Nội")) {
                        itemLicensePlateFee = LICENSE_PLATE_FEE_HCM_HN;
                    } else {
                        itemLicensePlateFee = LICENSE_PLATE_FEE_OTHER;
                    }
                    
                    // Phí đăng ký biển số (serviceFee): 1.5tr (cố định)
                    itemServiceFee = SERVICE_FEE_AMOUNT;
                    
                    // Other tax: 2.5tr (hardcode)
                    itemOtherTax = OTHER_TAX_AMOUNT;
                    
                    // Cộng dồn vào tổng
                    totalLicensePlateFee = totalLicensePlateFee.add(itemLicensePlateFee);
                    totalServiceFee = totalServiceFee.add(itemServiceFee);
                    totalOtherTax = totalOtherTax.add(itemOtherTax);
                }
                
                // Các loại phí khác = phí đăng ký biển số (serviceFee) + other tax
                BigDecimal itemOtherFees = itemServiceFee.add(itemOtherTax);
                totalTax = totalTax.add(itemLicensePlateFee).add(itemOtherFees);
                
                // Tính total price cho 1 xe
                BigDecimal priceForOneVehicle = calculateTotalPrice(
                        stock.getPriceOfStore(),
                        1, // Mỗi detail = 1 vehicle
                        itemLicensePlateFee,
                        itemServiceFee,
                        itemOtherTax,
                        discountPerVehicle
                );
                
                // Cộng vào tổng
                totalOrderPrice = totalOrderPrice.add(unitPrice);
                totalPromotions = totalPromotions.add(discountPerVehicle);
                finalAmount = finalAmount.add(priceForOneVehicle);
                
                // Create OrderDetail - mỗi detail = 1 vehicle, không có quantity field
                OrderDetail orderDetail = OrderDetail.builder()
                        // set đơn giá là giá tại cửa hàng KHÔNG bao gồm VAT
                        .unitPrice(unitPrice)
                        // quantity đã bị xóa, mỗi detail = 1 vehicle
                        .licensePlateFee(itemLicensePlateFee) // phí biển số cho 1 xe
                        .serviceFee(itemServiceFee) // phí đăng ký biển số cho 1 xe
                        .otherTax(itemOtherTax) // các loại thuế khác cho 1 xe
                        .discountAmount(discountPerVehicle)
                        .totalPrice(priceForOneVehicle) // tiền cho 1 xe
                        .createdAt(LocalDateTime.now())
                        .order(order)
                        .storeStock(stock)
                        .promotion(promotion)
                        .build();
                
                OrderDetail saved = orderDetailRepository.save(orderDetail);
                orderDetails.add(saved);
            }
        }
        
        // Xóa những order details không còn trong request
        for (List<OrderDetail> detailsToDelete : existingDetailsMap.values()) {
            for (OrderDetail detailToDelete : detailsToDelete) {
                orderDetailRepository.delete(detailToDelete);
                orderDetails.remove(detailToDelete);
            }
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
                        orderDetails.stream().map(od -> GetOrderDetailsResponse.builder()
                                .orderDetailId(od.getId())
                                .modelId(od.getStoreStock().getModelColor().getModel().getModelId())
                                .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                                .colorId(od.getStoreStock().getModelColor().getColor().getColorId())
                                .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                                .unitPrice(od.getUnitPrice())
                                .quantity(1) // Mỗi detail = 1 vehicle
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

    // Merge duplicate items trong request (cùng model + color) bằng cách cộng dồn quantity
    private Map<String, CreateOrderDetailsRequest> mergeDuplicateItem(CreateOrderWithItemsRequest request) {
        Map<String, CreateOrderDetailsRequest> mergedRequestItems = new HashMap<>();

        for (int i = 0; i < request.getOrderDetails().size(); i++) {
            CreateOrderDetailsRequest itemReq = request.getOrderDetails().get(i);

            // Validation: quantity phải > 0
            if (itemReq.getQuantity() <= 0) {
                throw new AppException(ErrorCode.INVALID_NUMBER, "Số lượng phải lớn hơn 0");
            }

            // Merge duplicate items: nếu đã có item với cùng model+color thì cộng dồn quantity
            String itemKey = itemReq.getModelId() + "_" + itemReq.getColorId();
            if (mergedRequestItems.containsKey(itemKey)) {
                CreateOrderDetailsRequest existingItem = mergedRequestItems.get(itemKey);
                // Cộng dồn quantity, giữ promotionId của item đầu tiên
                existingItem.setQuantity(existingItem.getQuantity() + itemReq.getQuantity());
            } else {
                mergedRequestItems.put(itemKey, itemReq);
            }
        }
        return mergedRequestItems;
    }


    @Override
    public List<GetOrderDetailsResponse> getOrderDetailsByOrderId(int orderId) {
        User currentUser = userService.getCurrentUserEntity();
        Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderIdAndOrder_Store_StoreId(orderId, currentStore.getStoreId());
        return orderDetails.stream().map(this::mapToDto).toList();
    }

    /**
     * Gán vehicles vào order details - Main method
     * 
     * Flow xử lý:
     * 1. Validate input: Kiểm tra danh sách assignments không rỗng
     * 2. Load data: Load tất cả order details và vehicles một lần (tối ưu performance)
     * 3. Validate business rules: Kiểm tra tất cả điều kiện trước khi gán
     * 4. Update order status: Chuyển order sang PENDING_DEPOSIT (đã gán xe, chờ đặt cọc)
     * 5. Assign vehicles: Thực hiện gán và cập nhật các thông tin liên quan
     * 
     * @param assignments Danh sách các assignment (orderDetailId -> vehicleId)
     * @return Danh sách order details đã được cập nhật
     */
    @Override
    public List<GetOrderDetailsResponse> assignVehiclesToOrderDetails(List<VehicleAssignment> assignments) {
        // ========== BƯỚC 1: VALIDATE INPUT ==========
        // Kiểm tra danh sách assignments không null và không rỗng
        if (assignments == null || assignments.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_NUMBER, "Danh sách gán xe không được rỗng");
        }
        
        // ========== BƯỚC 2: LOAD TẤT CẢ DATA CẦN THIẾT MỘT LẦN ==========
        // Load store hiện tại của user (để validate order details thuộc store này)
        Store currentStore = storeService.getCurrentStoreEntity(userService.getCurrentUserEntity().getUserId());
        
        // Load tất cả order details vào Map (key: orderDetailId)
        // Tối ưu: Load một lần, sử dụng lại nhiều lần thay vì query lại
        Map<Integer, OrderDetail> orderDetailMap = loadOrderDetails(assignments, currentStore);
        
        // Load tất cả vehicles vào Map (key: vehicleId)
        // Tối ưu: Load một lần, sử dụng lại nhiều lần thay vì query lại
        Map<Long, Vehicle> vehicleMap = loadVehicles(assignments);
        
        // ========== BƯỚC 3: VALIDATE BUSINESS RULES ==========
        // Validate tất cả điều kiện trước khi gán:
        // - Vehicle status phải là AVAILABLE
        // - Vehicle phải khớp model và color với order detail
        // - Không có vehicle nào bị trùng lặp trong request
        // - Vehicle chưa được gán cho order detail khác
        validateAssignments(assignments, orderDetailMap, vehicleMap, currentStore);
        
        // ========== BƯỚC 4: UPDATE ORDER STATUS ==========
        // Tất cả order details trong request phải thuộc cùng một order
        // Lấy order từ order detail đầu tiên (tất cả đều cùng một order)
        // Chuyển order status thành PENDING_DEPOSIT: đã gán xe, chờ khách hàng thanh toán đặt cọc
        Order order = orderDetailMap.values().iterator().next().getOrder();
        orderService.updateOrderStatus(order, OrderStatus.PENDING_DEPOSIT);
        
        // ========== BƯỚC 5: ASSIGN VEHICLES VÀ UPDATE CÁC THÔNG TIN LIÊN QUAN ==========
        // Thực hiện gán vehicle vào order detail và cập nhật:
        // - Vehicle status: AVAILABLE -> HOLDING
        // - Store stock: reservedQuantity + 1
        return assignVehiclesAndUpdate(assignments, orderDetailMap, vehicleMap);
    }
    
    /**
     * Load tất cả order details từ database và validate
     * 
     * @param assignments Danh sách assignments
     * @param currentStore Store hiện tại của user
     * @return Map chứa order details (key: orderDetailId, value: OrderDetail entity)
     */
    private Map<Integer, OrderDetail> loadOrderDetails(List<VehicleAssignment> assignments, Store currentStore) {
        Map<Integer, OrderDetail> orderDetailMap = new HashMap<>();
        
        for (VehicleAssignment assignment : assignments) {
            int orderDetailId = assignment.getOrderDetailId();
            
            // Load order detail từ database
            OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
            
            // Validate order detail thuộc store hiện tại
            // Bảo mật: Staff chỉ có thể gán xe cho order details của store mình
            if (orderDetail.getOrder().getStore().getStoreId() != currentStore.getStoreId()) {
                throw new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND);
            }
            
            // Thêm vào Map để sử dụng lại (tránh query lại nhiều lần)
            orderDetailMap.put(orderDetailId, orderDetail);
        }
        
        return orderDetailMap;
    }
    
    /**
     * Load tất cả vehicles từ database
     * 
     * @param assignments Danh sách assignments
     * @return Map chứa vehicles (key: vehicleId, value: Vehicle entity)
     */
    private Map<Long, Vehicle> loadVehicles(List<VehicleAssignment> assignments) {
        Map<Long, Vehicle> vehicleMap = new HashMap<>();
        
        for (VehicleAssignment assignment : assignments) {
            long vehicleId = assignment.getVehicleId();
            
            // Load vehicle từ service (có validation trong service)
            Vehicle vehicle = vehicleService.getVehicleEntityById(vehicleId);
            
            // Thêm vào Map để sử dụng lại (tránh query lại nhiều lần)
            vehicleMap.put(vehicleId, vehicle);
        }
        
        return vehicleMap;
    }
    
    /**
     * Validate tất cả business rules trước khi gán vehicles
     * 
     * Các validation được thực hiện:
     * 1. Vehicle status phải là AVAILABLE
     *    - Nếu vehicle đã được gán cho order detail khác, status đã là HOLDING (không còn AVAILABLE)
     *    - API get vehicle chỉ trả về vehicle có status AVAILABLE
     *    - Nên không cần check vehicle đã được gán chưa (đã được cover bởi status check)
     * 2. Vehicle phải khớp model và color với order detail
     * 3. Không có vehicle nào bị trùng lặp trong request (một vehicle không thể gán cho nhiều order detail)
     * 
     * @param assignments Danh sách assignments
     * @param orderDetailMap Map chứa order details đã load
     * @param vehicleMap Map chứa vehicles đã load
     * @param currentStore Store hiện tại (để validate nếu cần)
     */
    private void validateAssignments(List<VehicleAssignment> assignments, 
                                    Map<Integer, OrderDetail> orderDetailMap,
                                    Map<Long, Vehicle> vehicleMap,
                                    Store currentStore) {
        // ========== VALIDATE 1: Vehicle status và model/color match ==========
        // Kiểm tra từng assignment: vehicle phải available và khớp với order detail
        for (VehicleAssignment assignment : assignments) {
            OrderDetail orderDetail = orderDetailMap.get(assignment.getOrderDetailId());
            Vehicle vehicle = vehicleMap.get(assignment.getVehicleId());
            
            // Validate vehicle status = AVAILABLE
            // Note: Nếu vehicle đã được gán cho order detail khác, status đã là HOLDING
            // API get vehicle chỉ trả về AVAILABLE, nên không cần check vehicle đã được gán chưa
            if (vehicle.getStatus() != swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus.AVAILABLE) {
                String vin = vehicle.getVin() != null ? vehicle.getVin() : "N/A";
                throw new AppException(ErrorCode.VEHICLE_NOT_AVAILABLE, 
                    String.format("Xe có VIN %s (ID: %d) không có sẵn. Trạng thái hiện tại: %s", 
                        vin, vehicle.getVehicleId(), vehicle.getStatus()));
            }
            
            // Validate vehicle khớp model và color với order detail
            validateVehicleModelColorMatch(orderDetail, vehicle);
        }
        
        // ========== VALIDATE 2: Không có vehicle trùng lặp trong request ==========
        // Một vehicle không thể được gán cho nhiều order detail trong cùng một request
        validateNoDuplicateVehicles(assignments, vehicleMap);
    }
    
    /**
     * Validate vehicle khớp model và color với order detail
     * 
     * Vehicle được gán phải có cùng model và color với order detail
     * Ví dụ: Order detail yêu cầu "Tesla Model 3 - Đỏ" thì vehicle cũng phải là "Tesla Model 3 - Đỏ"
     * 
     * @param orderDetail Order detail cần gán vehicle
     * @param vehicle Vehicle được gán
     * @throws AppException nếu model hoặc color không khớp (có thông tin VIN và model/color)
     */
    private void validateVehicleModelColorMatch(OrderDetail orderDetail, Vehicle vehicle) {
        // Lấy model và color từ order detail (qua StoreStock -> ModelColor)
        int orderModelId = orderDetail.getStoreStock().getModelColor().getModel().getModelId();
        int orderColorId = orderDetail.getStoreStock().getModelColor().getColor().getColorId();
        String orderModelName = orderDetail.getStoreStock().getModelColor().getModel().getModelName();
        String orderColorName = orderDetail.getStoreStock().getModelColor().getColor().getColorName();
        
        // Lấy model và color từ vehicle (qua StoreStock -> ModelColor)
        int vehicleModelId = vehicle.getStoreStock().getModelColor().getModel().getModelId();
        int vehicleColorId = vehicle.getStoreStock().getModelColor().getColor().getColorId();
        String vehicleModelName = vehicle.getStoreStock().getModelColor().getModel().getModelName();
        String vehicleColorName = vehicle.getStoreStock().getModelColor().getColor().getColorName();
        
        // So sánh: cả model và color phải khớp
        if (orderModelId != vehicleModelId || orderColorId != vehicleColorId) {
            String vin = vehicle.getVin() != null ? vehicle.getVin() : "N/A";
            throw new AppException(ErrorCode.VEHICLE_NOT_MATCH, 
                String.format("Xe có VIN %s (ID: %d) không khớp với đơn hàng. " +
                    "Đơn hàng yêu cầu: %s - %s, nhưng xe là: %s - %s",
                    vin, vehicle.getVehicleId(), 
                    orderModelName, orderColorName,
                    vehicleModelName, vehicleColorName));
        }
    }
    
    /**
     * Validate không có vehicle nào bị trùng lặp trong request
     * 
     * Một vehicle không thể được gán cho nhiều order detail trong cùng một request
     * Ví dụ: Không thể gán vehicle ID 100 cho cả orderDetail 1 và orderDetail 2
     * 
     * @param assignments Danh sách assignments
     * @param vehicleMap Map chứa vehicles để lấy thông tin VIN
     * @throws AppException nếu có vehicle bị trùng lặp
     */
    private void validateNoDuplicateVehicles(List<VehicleAssignment> assignments, Map<Long, Vehicle> vehicleMap) {
        Map<Long, Integer> vehicleUsageMap = new HashMap<>();
        
        for (VehicleAssignment assignment : assignments) {
            long vehicleId = assignment.getVehicleId();
            
            // Nếu vehicle đã xuất hiện trong Map -> bị trùng lặp
            if (vehicleUsageMap.containsKey(vehicleId)) {
                Vehicle vehicle = vehicleMap.get(vehicleId);
                String vin = vehicle.getVin() != null ? vehicle.getVin() : "N/A";
                int firstOrderDetailId = vehicleUsageMap.get(vehicleId);
                throw new AppException(ErrorCode.VEHICLE_ALREADY_ASSIGNED, 
                    String.format("Xe có VIN %s (ID: %d) được gán cho nhiều order detail trong cùng một request. " +
                        "Đã được gán cho order detail %d, không thể gán thêm cho order detail %d",
                        vin, vehicleId, firstOrderDetailId, assignment.getOrderDetailId()));
            }
            
            // Lưu vehicle vào Map để track
            vehicleUsageMap.put(vehicleId, assignment.getOrderDetailId());
        }
    }
    
    
    /**
     * Gán vehicles vào order details và cập nhật các thông tin liên quan
     * 
     * Flow xử lý cho mỗi assignment:
     * 1. Gán vehicle vào order detail: Liên kết vehicle cụ thể với order detail
     * 2. Update vehicle status: Chuyển status từ AVAILABLE -> HOLDING (xe đã được gán, chờ thanh toán đặt cọc)
     * 
     * Lưu ý: KHÔNG reserve stock ở đây vì đã được reserve khi confirmOrder() (theo quantity của order detail)
     * 
     * @param assignments Danh sách các assignment cần xử lý
     * @param orderDetailMap Map chứa order details đã được load sẵn (key: orderDetailId)
     * @param vehicleMap Map chứa vehicles đã được load sẵn (key: vehicleId)
     * @return Danh sách order details đã được cập nhật (dạng DTO)
     */
    private List<GetOrderDetailsResponse> assignVehiclesAndUpdate(List<VehicleAssignment> assignments,
                                                                  Map<Integer, OrderDetail> orderDetailMap,
                                                                  Map<Long, Vehicle> vehicleMap) {
        List<GetOrderDetailsResponse> results = new java.util.ArrayList<>();
        
        for (VehicleAssignment assignment : assignments) {
            // Lấy order detail và vehicle từ Map (đã được load và validate trước đó)
            OrderDetail orderDetail = orderDetailMap.get(assignment.getOrderDetailId());
            Vehicle vehicle = vehicleMap.get(assignment.getVehicleId());
            
            // ========== BƯỚC 1: GÁN VEHICLE VÀO ORDER DETAIL ==========
            // Liên kết vehicle cụ thể với order detail này
            // Mối quan hệ OneToOne: mỗi order detail sẽ có một vehicle cụ thể (VIN)
            orderDetail.setVehicle(vehicle);
            orderDetail.setUpdatedAt(LocalDateTime.now());
            orderDetailRepository.save(orderDetail);
            
            // ========== BƯỚC 2: UPDATE VEHICLE STATUS ==========
            // Chuyển status từ AVAILABLE -> HOLDING
            // HOLDING: Xe đã được gán cho đơn hàng, đang chờ khách hàng thanh toán đặt cọc
            // Khi status = HOLDING, xe không còn available để gán cho đơn hàng khác
            vehicleService.updateVehicleStatusById(assignment.getVehicleId(), 
                swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus.HOLDING);
            
            // Lưu ý: KHÔNG reserve stock ở đây vì đã được reserve khi confirmOrder()
            // Stock đã được reserve theo quantity của order detail khi order chuyển sang CONFIRMED
            
            // Thêm vào kết quả trả về
            results.add(mapToDto(orderDetail));
        }
        
        return results;
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
        // Mỗi detail = 1 vehicle, quantity luôn = 1
        // Các loại phí khác = serviceFee + otherTax
        BigDecimal otherFees = serviceFee.add(otherTax);
        // quantity = 1 vì mỗi detail = 1 vehicle
        BigDecimal totalPrice = priceOfStore
                .add(licensePlateFee)
                .add(otherFees)
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
                .quantity(1) // Mỗi detail = 1 vehicle
                .licensePlateFee(od.getLicensePlateFee()) // phí biển số
                .serviceFee(od.getServiceFee()) // phí đăng ký biển số
                .otherTax(od.getOtherTax()) // thuế khác
                .otherFees(od.getServiceFee().add(od.getOtherTax())) // phí khác (gồm phí đăng ký biển số + thuế khác)
                .promotionId(od.getPromotion() != null ? od.getPromotion().getPromotionId() : null)
                .promotionName(od.getPromotion() != null ? od.getPromotion().getPromotionName() : null)
                .discountAmount(od.getDiscountAmount())
                .totalPrice(od.getTotalPrice())
                .build();
    }
}