package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.request.contract.CreateContractRequest;
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
        // Validate order
        Order order = orderService.getOrderEntityById(request.getOrderId());
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
        contract.setContractCode("CTR-" + String.format("%06d", contract.getContractId()));
        Contract saved = contractRepository.save(contract);

        // Generate PDF from template (để in ra)
//        String unsignedPdfUrl = pdfGeneratorService.generateContractPdf(saved);

        // Update order
        order.setStatus(OrderStatus.CONTRACT_PENDING); // Chờ ký hợp đồng
        orderService.updateOrder(order);
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
    public ContractDto getContractById(int id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        return mapToDto(contract);
    }

    @Override
    public ContractDto addFileUrlContract(int id, String fileUrl) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        // Validate contractFileUrl unique
        if (contractRepository.existsByContractFileUrl(fileUrl)) {
            throw new AppException(ErrorCode.CONTRACT_FILE_URL_EXISTED);
        }

        contract.setContractFileUrl(fileUrl);
        contract.setStatus(ContractStatus.SIGNED); // Đã ký
        contract.setUpdatedAt(LocalDateTime.now());

        Order order = contract.getOrder();
        order.setStatus(OrderStatus.COMPLETED); // Hoàn tất
        orderService.updateOrder(order);

        contractRepository.save(contract);
        return mapToDto(contract);
    }

    @Override
    public List<GetContractResponse> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        return contracts.stream().map(contract -> GetContractResponse.builder()
                        .contractId(contract.getContractId())
                        .contractCode(contract.getContractCode())
                        .contractDate(contract.getContractDate())
                        .contractFileUrl(contract.getContractFileUrl())
                        .status(contract.getStatus().name())
                        .depositPrice(contract.getDepositPrice())
                        .totalPayment(contract.getTotalPayment())
                        .remainPrice(contract.getRemainPrice())
                        .terms(contract.getTerms())
                        .orderId(contract.getOrder().getOrderId())
                        .build())
                .toList();
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
//    @Override
//    public void deleteContractById(int id) {
//        Contract contract = contractRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
//        contractRepository.delete(contract);
//    }
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
//                .createdAt(contract.getCreatedAt().toString())
//                .updatedAt(contract.getUpdatedAt().toString())
                .orderId(contract.getOrder().getOrderId())
                .build();
    }
}
