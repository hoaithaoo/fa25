package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractType;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.request.contract.CreateContractRequest;
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
        // Validate contractType không được để trống
        if (request.getContractType() == null) {
            throw new AppException(ErrorCode.CONTRACT_TYPE_REQUIRED);
        }

        // Validate order, không thể tạo contract nếu chưa confirm order
        Order order = orderService.getOrderEntityById(request.getOrderId());
        if (!order.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new AppException(ErrorCode.ORDER_NOT_IN_CONFIRMED_STATUS);
        }

        // Kiểm tra contract cùng loại đã tồn tại chưa
        if (request.getContractType() == ContractType.DEPOSIT) {
            if (contractRepository.existsByOrder_OrderIdAndContractType(request.getOrderId(), ContractType.DEPOSIT)) {
                throw new AppException(ErrorCode.DEPOSIT_CONTRACT_ALREADY_EXISTS);
            }
        } else if (request.getContractType() == ContractType.SALE) {
            if (contractRepository.existsByOrder_OrderIdAndContractType(request.getOrderId(), ContractType.SALE)) {
                throw new AppException(ErrorCode.SALE_CONTRACT_ALREADY_EXISTS);
            }
        }

        User staff = userService.getCurrentUserEntity();
        BigDecimal orderTotalPayment = order.getTotalPayment();

        // Tính toán totalPayment dựa trên loại hợp đồng
        BigDecimal totalPayment;

        if (request.getContractType() == ContractType.DEPOSIT) {
            // Hợp đồng đặt cọc: chỉ tính 20% của tổng tiền
            totalPayment = orderTotalPayment.multiply(DEPOSIT_PERCENTAGE);
        } else {
            // Hợp đồng mua bán: tính toàn bộ
            totalPayment = orderTotalPayment;
        }

        // Create draft contract
        Contract contract = Contract.builder()
                .contractDate(LocalDateTime.now().toLocalDate())
                .contractType(request.getContractType())
                .status(ContractStatus.DRAFT)
                .totalPayment(totalPayment)
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
//        order.setContract(saved);
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
    public ContractDto getContractById(int id) {
        // getContractEntityById đã có authorization check
        Contract contract = getContractEntityById(id);
        return mapToDto(contract);
    }

    @Override
    public ContractDto getContractDetailById(int id) {
        // getContractEntityById đã có authorization check
        Contract contract = getContractEntityById(id);
        return mapToDto(contract);
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
        contract.setUpdatedAt(LocalDateTime.now());

        // Nếu totalPayment = 0, tự động set FULLY_PAID, không cần SIGNED
        if (contract.getTotalPayment().compareTo(BigDecimal.ZERO) <= 0) {
            contract.setStatus(ContractStatus.FULLY_PAID);
            orderService.updateOrderStatus(contract.getOrder(), OrderStatus.FULLY_PAID);
        } else {
            contract.setStatus(ContractStatus.SIGNED); // Đã ký
            orderService.updateOrderStatus(contract.getOrder(), OrderStatus.CONTRACT_SIGNED);
        }

        contractRepository.save(contract);
        return mapToDto(contract);
    }

    @Override
    public List<ContractDto> getAllContracts() {
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
                    Order order = contract.getOrder();
                    Customer customer = order.getCustomer();
                    User staff = order.getUser();
                    Store store = order.getStore();
                    
                    return ContractDto.builder()
                            .contractId(contract.getContractId())
                            .contractCode(contract.getContractCode())
                            .contractType(contract.getContractType() != null ? contract.getContractType().name() : null)
                            .contractDate(contract.getContractDate())
                            .contractFileUrl(contract.getContractFileUrl())
                            .status(contract.getStatus() != null ? contract.getStatus().name() : null)
                            .totalPayment(contract.getTotalPayment())
                            .uploadedBy(contract.getUploadedBy())
                            .orderId(order.getOrderId())
                            .orderCode(order.getOrderCode())
                            .customerId(customer.getCustomerId())
                            .customerName(customer.getFullName())
                            .staffId(staff.getUserId())
                            .staffName(staff.getFullName())
                            .storeId(store != null ? store.getStoreId() : null)
                            .storeName(store != null ? store.getStoreName() : null)
                            .storeAddress(store != null ? store.getAddress() : null)
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

    /**
     * Tính số tiền còn lại phải trả dựa trên payment status
     * Tính tổng số tiền đã thanh toán (completed payments) và trừ đi totalPayment của contract
     */
//    private BigDecimal calculateRemainingAmountToPay(Contract contract) {
//        List<swp391.fa25.saleElectricVehicle.entity.Payment> payments = contract.getPayments();
//
//        // Tính tổng số tiền đã thanh toán thành công
//        BigDecimal totalPaid = payments.stream()
//                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
//                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Số tiền còn lại = totalPayment - totalPaid
//        BigDecimal remaining = contract.getTotalPayment().subtract(totalPaid);
//
//        // Đảm bảo không trả về số âm
//        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
//    }

    private ContractDto mapToDto(Contract contract) {
        Order order = contract.getOrder();
        Customer customer = order.getCustomer();
        User staff = order.getUser();
        Store store = order.getStore();
        
        return ContractDto.builder()
                .contractId(contract.getContractId())
                .contractCode(contract.getContractCode())
                .contractType(contract.getContractType() != null ? contract.getContractType().name() : null)
                .contractDate(contract.getContractDate())
                .contractFileUrl(contract.getContractFileUrl())
                .status(contract.getStatus() != null ? contract.getStatus().name() : null)
                .totalPayment(contract.getTotalPayment())
                .uploadedBy(contract.getUploadedBy())
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .customerId(customer.getCustomerId())
                .customerName(customer.getFullName())
                .staffId(staff.getUserId())
                .staffName(staff.getFullName())
                .storeId(store != null ? store.getStoreId() : null)
                .storeName(store != null ? store.getStoreName() : null)
                .storeAddress(store != null ? store.getAddress() : null)
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
}
