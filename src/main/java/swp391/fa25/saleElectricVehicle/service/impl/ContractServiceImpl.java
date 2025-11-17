package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDetailsDto;
import swp391.fa25.saleElectricVehicle.payload.request.contract.CreateContractRequest;
import swp391.fa25.saleElectricVehicle.payload.response.contract.GetContractDetailResponse;
import swp391.fa25.saleElectricVehicle.payload.response.contract.GetContractResponse;
import swp391.fa25.saleElectricVehicle.repository.ContractRepository;
import swp391.fa25.saleElectricVehicle.service.ContractService;
import swp391.fa25.saleElectricVehicle.service.OrderService;
import swp391.fa25.saleElectricVehicle.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    private final BigDecimal DEPOSIT_PERCENTAGE = BigDecimal.valueOf(0.2); // 20% deposit

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

//    @Autowired
//    private PdfGeneratorService pdfGeneratorService;
//
//    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
    @Override
    public ContractDto createDraftContract(CreateContractRequest request) {
        // Validate order, không thể tạo contract nếu chưa confirm order
        Order order = orderService.getOrderEntityById(request.getOrderId());
        if (!order.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new AppException(ErrorCode.ORDER_NOT_IN_CONFIRMED_STATUS);
        }
        User staff = userService.getCurrentUserEntity();

        // Create draft contract
        Contract contract = Contract.builder()
                .contractDate(LocalDateTime.now().toLocalDate())
                .status(ContractStatus.DRAFT)
                .depositPrice(order.getTotalPayment().multiply(DEPOSIT_PERCENTAGE))
                .totalPayment(order.getTotalPayment())
                .remainPrice(order.getTotalPayment()
                        .subtract(order.getTotalPayment().multiply(DEPOSIT_PERCENTAGE)))
                .uploadedBy(staff.getFullName())
                .createdAt(LocalDateTime.now())
                .order(order)
                .build();

        // Save to get contractId
        contractRepository.save(contract);
        contract.setContractCode("CTR" + String.format("%06d", contract.getContractId()));
        Contract saved = contractRepository.save(contract);

        // Generate PDF from template (để in ra)
//        String unsignedPdfUrl = pdfGeneratorService.generateContractPdf(saved);

//         Update order
        order.setContract(saved);
        order.setStatus(OrderStatus.CONTRACT_PENDING); // Chờ ký hợp đồng
        orderService.updateOrder(order); // phải lưu 2 chiều
//        orderService.updateOrderStatus(saved.getOrder(), OrderStatus.CONTRACT_PENDING); // Chờ ký hợp đồng
        return mapToDto(saved);

//        // Validate contractFileUrl unique
//        if (contractRepository.existsByContractFileUrl(contractDto.getContractFileUrl())) {
//            throw new AppException(ErrorCode.CONTRACT_FILE_URL_EXISTED);
//        }
//
//        // Validate Order exists
//        Order order = orderRepository.findById(contractDto.getOrderId())
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
//
//        // Validate depositPrice and remainPrice
//        if (contractDto.getDepositPrice() != null && contractDto.getDepositPrice().compareTo(BigDecimal.ZERO) < 0) {
//            throw new AppException(ErrorCode.INVALID_NUMBER);
//        }
//
//        if (contractDto.getTotalPayment() == null || contractDto.getTotalPayment().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new AppException(ErrorCode.INVALID_NUMBER);
//        }
//
//        // Calculate remainPrice if not provided
//        BigDecimal remainPrice;
//        if (contractDto.getDepositPrice() != null) {
//            remainPrice = contractDto.getTotalPayment().subtract(contractDto.getDepositPrice());
//        } else {
//            remainPrice = contractDto.getTotalPayment();
//        }
//
//        // Set default status if not provided
//        Contract.ContractStatus status;
//        if (contractDto.getStatus() != null) {
//            status = contractDto.getStatus();
//        } else {
//            status = Contract.ContractStatus.DRAFT;
//        }
//
//        Contract newContract = Contract.builder()
//                .contractDate(contractDto.getContractDate())
//                .contractFileUrl(contractDto.getContractFileUrl())
//                .status(status)
//                .depositPrice(contractDto.getDepositPrice())
//                .totalPayment(contractDto.getTotalPayment())
//                .remainPrice(remainPrice)
//                .terms(contractDto.getTerms())
//                .uploadedBy(contractDto.getUploadedBy())
//                .createdAt(LocalDateTime.now().format(formatter))
//                .order(order)
//                .build();
//
//        contractRepository.save(newContract);
//        return mapToDto(newContract);
    }

    @Override
    public Contract getContractEntityById(int id) {
        User currentUser = userService.getCurrentUserEntity();
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        int storeId = currentUser.getStore().getStoreId();

        Contract contract;
        if (isManager) {
            // Manager: chỉ check store
            contract = contractRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
            
            int contractStoreId = contract.getOrder().getStore().getStoreId();
            if (contractStoreId != storeId) {
                throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
            }
        } else {
            // Staff: check cả store và userId
            contract = contractRepository.findByOrder_Store_StoreIdAndOrder_User_UserIdAndContractId(
                    storeId, currentUser.getUserId(), id);
            if (contract == null) {
                throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
            }
        }

        return contract;
    }

    @Override
    public Contract getContractEntityByContractCode(String contractCode) {
        Contract contract = contractRepository.findContractByContractCode(contractCode);
//        if (contract == null) {
//            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
//        }
        return contract;
    }

    @Override
    public ContractDto getContractById(int id) {
        // getContractEntityById đã có authorization check
        Contract contract = getContractEntityById(id);
        return mapToDto(contract);
    }

    @Override
    public GetContractDetailResponse getContractDetailById(int id) {
        // getContractEntityById đã có authorization check
        Contract contract = getContractEntityById(id);
        Order order = contract.getOrder();
        Customer customer = order.getCustomer();
        User staff = order.getUser();
        Store store = order.getStore(); // Lấy store từ order thay vì từ staff
        
        // Map customer
        CustomerDto customerDto = CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .fullName(customer.getFullName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .identificationNumber(customer.getIdentificationNumber())
                .createdAt(customer.getCreatedAt())
                .build();
        
        // Map order details
        List<OrderDetailsDto> orderDetails = order.getOrderDetails().stream()
                .map(od -> OrderDetailsDto.builder()
                        .modelName(od.getStoreStock().getModelColor().getModel().getModelName())
                        .modelYear(od.getStoreStock().getModelColor().getModel().getModelYear())
                        .seatingCapacity(od.getStoreStock().getModelColor().getModel().getSeatingCapacity())
                        .bodyType(od.getStoreStock().getModelColor().getModel().getBodyType().name())
                        .colorName(od.getStoreStock().getModelColor().getColor().getColorName())
                        .quantity(od.getQuantity())
                        .unitPrice(od.getUnitPrice())
                        .discount(od.getDiscountAmount())
                        .totalTax(od.getLicensePlateFee().add(od.getRegistrationFee()))
                        .totalPrice(od.getTotalPrice())
                        .build())
                .toList();
        
        // Map payments
        List<GetContractDetailResponse.PaymentInfo> payments = contract.getPayments().stream()
                .map(payment -> GetContractDetailResponse.PaymentInfo.builder()
                        .paymentId(payment.getPaymentId())
                        .paymentCode(payment.getPaymentCode())
                        .remainPrice(payment.getRemainPrice())
                        .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                        .paymentType(payment.getPaymentType() != null ? payment.getPaymentType().name() : null)
                        .paymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null)
                        .amount(payment.getAmount())
                        .createdAt(payment.getCreatedAt())
                        .updatedAt(payment.getUpdatedAt())
                        .build())
                .toList();
        
        // Tính số tiền còn lại phải trả
        BigDecimal remainingAmountToPay = calculateRemainingAmountToPay(contract);
        
        return GetContractDetailResponse.builder()
                // Contract info
                .contractId(contract.getContractId())
                .contractCode(contract.getContractCode())
                .contractDate(contract.getContractDate())
                .status(contract.getStatus() != null ? contract.getStatus().name() : null)
                .depositPrice(contract.getDepositPrice())
                .totalPayment(contract.getTotalPayment())
                .remainPrice(contract.getRemainPrice())
                .remainingAmountToPay(remainingAmountToPay)
                .terms(contract.getTerms())
                .contractFileUrl(contract.getContractFileUrl())
                .uploadedBy(contract.getUploadedBy())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                // Order info
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderStatus(order.getStatus() != null ? order.getStatus().name() : null)
                .orderDate(order.getOrderDate())
                .orderTotalPrice(order.getTotalPrice())
                .orderTotalTaxPrice(order.getTotalTaxPrice())
                .orderTotalPromotionAmount(order.getTotalPromotionAmount())
                .orderTotalPayment(order.getTotalPayment())
                // Customer info
                .customer(customerDto)
                // Staff info
                .staffId(staff.getUserId())
                .staffName(staff.getFullName())
                // Store info
                .storeId(store != null ? store.getStoreId() : 0)
                .storeName(store != null ? store.getStoreName() : null)
                .storeAddress(store != null ? store.getAddress() : null)
                // Order details
                .orderDetails(orderDetails)
                // Payments
                .payments(payments)
                .build();
    }

    @Override
    public ContractDto addFileUrlContract(int id, String fileUrl) {
        // getContractEntityById đã có authorization check
        Contract contract = getContractEntityById(id);

        // Validate contractFileUrl unique
        if (contractRepository.existsByContractFileUrl(fileUrl)) {
            throw new AppException(ErrorCode.CONTRACT_FILE_URL_EXISTED);
        }

        contract.setContractFileUrl(fileUrl);
        contract.setStatus(ContractStatus.SIGNED); // Đã ký
        contract.setUpdatedAt(LocalDateTime.now());

//        Order order = contract.getOrder();
//        order.setStatus(OrderStatus.CONTRACT_SIGNED); // Hoàn tất
//        orderService.updateOrder(order);

        orderService.updateOrderStatus(contract.getOrder(), OrderStatus.CONTRACT_SIGNED); // Hoàn tất

        contractRepository.save(contract);
        return mapToDto(contract);
    }

    @Override
    public List<GetContractResponse> getAllContracts() {
        // Lấy user hiện tại và store của user
        User currentUser = userService.getCurrentUserEntity();
        if (currentUser.getStore() == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }
        
        int storeId = currentUser.getStore().getStoreId();
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        
        List<Contract> contracts;
        if (isManager) {
            // Manager: xem tất cả contracts trong store
            contracts = contractRepository.findByOrder_Store_StoreId(storeId);
        } else {
            // Staff: chỉ xem contracts của chính họ trong store
            contracts = contractRepository.findByOrder_Store_StoreIdAndOrder_User_UserId(
                    storeId, currentUser.getUserId());
        }
        
        return contracts.stream().map(contract -> {
                    BigDecimal remainingAmountToPay = calculateRemainingAmountToPay(contract);
                    return GetContractResponse.builder()
                            .contractId(contract.getContractId())
                            .contractCode(contract.getContractCode())
                            .contractDate(contract.getContractDate())
                            .contractFileUrl(contract.getContractFileUrl())
                            .status(contract.getStatus().name())
                            .depositPrice(contract.getDepositPrice())
                            .totalPayment(contract.getTotalPayment())
                            .remainPrice(contract.getRemainPrice())
                            .remainingAmountToPay(remainingAmountToPay)
//                        .terms(contract.getTerms())
                            .orderId(contract.getOrder().getOrderId())
                            .orderCode(contract.getOrder().getOrderCode())
                            .customerId(contract.getOrder().getCustomer().getCustomerId())
                            .customerName(contract.getOrder().getCustomer().getFullName())
                            .createdAt(contract.getCreatedAt())
                            .updatedAt(contract.getUpdatedAt())
                            .build();
                })
                .toList();
    }

    // không cần kiểm tra vì gọi nội bộ
    @Override
    public void updateContractStatus(Contract contract, ContractStatus status) {
        contract.setStatus(status);
        contractRepository.save(contract);
    }

    //    @Override
//    public ContractDto getContractByFileUrl(String fileUrl) {
//        Contract contract = contractRepository.findByContractFileUrl(fileUrl);
//        if (contract == null) {
//            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
//        }
//        return mapToDto(contract);
//    }
//
//    @Override
//    public List<ContractDto> getAllContracts() {
//        List<Contract> contracts = contractRepository.findAll();
//        return contracts.stream().map(this::mapToDto).toList();
//    }
//
//    @Override
//    public List<ContractDto> getContractsByStatus(Contract.ContractStatus status) {
//        List<Contract> contracts = contractRepository.findByStatus(status);
//        return contracts.stream().map(this::mapToDto).toList();
//    }
//
    @Override
    public void deleteContractById(int id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        if (contract.getStatus() == ContractStatus.SIGNED) {
            throw new AppException(ErrorCode.CANNOT_DELETE_SIGNED_CONTRACT);
        }
        contractRepository.delete(contract);
    }
//
//    @Override
//    public ContractDto updateContract(int id, ContractDto contractDto) {
//        Contract existingContract = contractRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
//
//        // Update contractFileUrl if provided and different
//        if (contractDto.getContractFileUrl() != null
//                && !contractDto.getContractFileUrl().trim().isEmpty()
//                && !contractDto.getContractFileUrl().equals(existingContract.getContractFileUrl())) {
//            if (contractRepository.existsByContractFileUrl(contractDto.getContractFileUrl())) {
//                throw new AppException(ErrorCode.CONTRACT_FILE_URL_EXISTED);
//            }
//            existingContract.setContractFileUrl(contractDto.getContractFileUrl());
//        }
//
//        // Update contractDate if provided
//        if (contractDto.getContractDate() != null) {
//            existingContract.setContractDate(contractDto.getContractDate());
//        }
//
//        // Update status if provided
//        if (contractDto.getStatus() != null) {
//            existingContract.setStatus(contractDto.getStatus());
//        }
//
//        // Update depositPrice if provided
//        if (contractDto.getDepositPrice() != null) {
//            if (contractDto.getDepositPrice().compareTo(BigDecimal.ZERO) < 0) {
//                throw new AppException(ErrorCode.INVALID_NUMBER);
//            }
//            existingContract.setDepositPrice(contractDto.getDepositPrice());
//        }
//
//        // Update totalPayment if provided
//        if (contractDto.getTotalPayment() != null) {
//            if (contractDto.getTotalPayment().compareTo(BigDecimal.ZERO) <= 0) {
//                throw new AppException(ErrorCode.INVALID_NUMBER);
//            }
//            existingContract.setTotalPayment(contractDto.getTotalPayment());
//        }
//
//        // Recalculate remainPrice
//        BigDecimal depositPrice;
//        if (existingContract.getDepositPrice() != null) {
//            depositPrice = existingContract.getDepositPrice();
//        } else {
//            depositPrice = BigDecimal.ZERO;
//        }
//        existingContract.setRemainPrice(existingContract.getTotalPayment().subtract(depositPrice));
//
//        // Update terms if provided
//        if (contractDto.getTerms() != null && !contractDto.getTerms().trim().isEmpty()) {
//            existingContract.setTerms(contractDto.getTerms());
//        }
//
//        // Update uploadedBy if provided
//        if (contractDto.getUploadedBy() != null && !contractDto.getUploadedBy().trim().isEmpty()) {
//            existingContract.setUploadedBy(contractDto.getUploadedBy());
//        }
//
//        existingContract.setUpdatedAt(LocalDateTime.now().format(formatter));
//
//        contractRepository.save(existingContract);
//        return mapToDto(existingContract);
//    }
//
//    @Override
//    public ContractDto updateContractStatus(int id, Contract.ContractStatus status) {
//        Contract contract = contractRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
//
//        contract.setStatus(status);
//        contract.setUpdatedAt(LocalDateTime.now().format(formatter));
//
//        contractRepository.save(contract);
//        return mapToDto(contract);
//    }
//
    /**
     * Tính số tiền còn lại phải trả dựa trên payment status
     * - Nếu chưa trả cọc (deposit) lẫn balance: remainingAmountToPay = totalPayment
     * - Nếu đã trả cọc nhưng chưa trả balance: remainingAmountToPay = remainPrice (balance)
     * - Nếu đã trả cả 2: remainingAmountToPay = 0
     */
    private BigDecimal calculateRemainingAmountToPay(Contract contract) {
        List<swp391.fa25.saleElectricVehicle.entity.Payment> payments = contract.getPayments();
        
        // Kiểm tra xem có payment DEPOSIT completed chưa
        boolean hasCompletedDeposit = payments.stream()
                .anyMatch(p -> p.getPaymentType() == PaymentType.DEPOSIT 
                        && p.getStatus() == PaymentStatus.COMPLETED);
        
        // Kiểm tra xem có payment BALANCE completed chưa
        boolean hasCompletedBalance = payments.stream()
                .anyMatch(p -> p.getPaymentType() == PaymentType.BALANCE 
                        && p.getStatus() == PaymentStatus.COMPLETED);
        
        if (!hasCompletedDeposit && !hasCompletedBalance) {
            // Chưa trả cọc lẫn balance: còn lại toàn bộ tiền cần phải trả
            return contract.getTotalPayment();
        } else if (hasCompletedDeposit && !hasCompletedBalance) {
            // Đã trả cọc nhưng chưa trả balance: còn lại là balance
            return contract.getRemainPrice();
        } else {
            // Đã trả cả 2: còn lại là 0
            return BigDecimal.ZERO;
        }
    }

    private ContractDto mapToDto(Contract contract) {
        return ContractDto.builder()
                .contractId(contract.getContractId())
                .contractCode(contract.getContractCode())
                .contractDate(contract.getContractDate())
//                .contractFileUrl(contract.getContractFileUrl())
                .status(contract.getStatus().name())
                .depositPrice(contract.getDepositPrice())
                .totalPayment(contract.getTotalPayment())
                .remainPrice(contract.getRemainPrice())
                .terms(contract.getTerms())
//                .uploadedBy(contract.getUploadedBy())
                .orderId(contract.getOrder().getOrderId())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
}
