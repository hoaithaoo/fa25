package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.repository.ContractRepository;
import swp391.fa25.saleElectricVehicle.repository.OrderRepository;
import swp391.fa25.saleElectricVehicle.service.ContractService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ContractDto createContract(ContractDto contractDto) {
        // Validate contractFileUrl unique
        if (contractRepository.existsByContractFileUrl(contractDto.getContractFileUrl())) {
            throw new AppException(ErrorCode.CONTRACT_FILE_URL_EXISTED);
        }

        // Validate Order exists
        Order order = orderRepository.findById(contractDto.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

        // Validate depositPrice and remainPrice
        if (contractDto.getDepositPrice() != null && contractDto.getDepositPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        if (contractDto.getTotalPayment() == null || contractDto.getTotalPayment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }

        // Calculate remainPrice if not provided
        BigDecimal remainPrice;
        if (contractDto.getDepositPrice() != null) {
            remainPrice = contractDto.getTotalPayment().subtract(contractDto.getDepositPrice());
        } else {
            remainPrice = contractDto.getTotalPayment();
        }

        // Set default status if not provided
        Contract.ContractStatus status;
        if (contractDto.getStatus() != null) {
            status = contractDto.getStatus();
        } else {
            status = Contract.ContractStatus.DRAFT;
        }

        Contract newContract = Contract.builder()
                .contractDate(contractDto.getContractDate())
                .contractFileUrl(contractDto.getContractFileUrl())
                .status(status)
                .depositPrice(contractDto.getDepositPrice())
                .totalPayment(contractDto.getTotalPayment())
                .remainPrice(remainPrice)
                .terms(contractDto.getTerms())
                .uploadedBy(contractDto.getUploadedBy())
                .createdAt(LocalDateTime.now().format(formatter))
                .order(order)
                .build();

        contractRepository.save(newContract);
        return mapToDto(newContract);
    }

    @Override
    public ContractDto getContractById(int id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        return mapToDto(contract);
    }

    @Override
    public ContractDto getContractByFileUrl(String fileUrl) {
        Contract contract = contractRepository.findByContractFileUrl(fileUrl);
        if (contract == null) {
            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
        }
        return mapToDto(contract);
    }

    @Override
    public List<ContractDto> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        return contracts.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ContractDto> getContractsByStatus(Contract.ContractStatus status) {
        List<Contract> contracts = contractRepository.findByStatus(status);
        return contracts.stream().map(this::mapToDto).toList();
    }

    @Override
    public void deleteContractById(int id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        contractRepository.delete(contract);
    }

    @Override
    public ContractDto updateContract(int id, ContractDto contractDto) {
        Contract existingContract = contractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        // Update contractFileUrl if provided and different
        if (contractDto.getContractFileUrl() != null
                && !contractDto.getContractFileUrl().trim().isEmpty()
                && !contractDto.getContractFileUrl().equals(existingContract.getContractFileUrl())) {
            if (contractRepository.existsByContractFileUrl(contractDto.getContractFileUrl())) {
                throw new AppException(ErrorCode.CONTRACT_FILE_URL_EXISTED);
            }
            existingContract.setContractFileUrl(contractDto.getContractFileUrl());
        }

        // Update contractDate if provided
        if (contractDto.getContractDate() != null) {
            existingContract.setContractDate(contractDto.getContractDate());
        }

        // Update status if provided
        if (contractDto.getStatus() != null) {
            existingContract.setStatus(contractDto.getStatus());
        }

        // Update depositPrice if provided
        if (contractDto.getDepositPrice() != null) {
            if (contractDto.getDepositPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new AppException(ErrorCode.INVALID_NUMBER);
            }
            existingContract.setDepositPrice(contractDto.getDepositPrice());
        }

        // Update totalPayment if provided
        if (contractDto.getTotalPayment() != null) {
            if (contractDto.getTotalPayment().compareTo(BigDecimal.ZERO) <= 0) {
                throw new AppException(ErrorCode.INVALID_NUMBER);
            }
            existingContract.setTotalPayment(contractDto.getTotalPayment());
        }

        // Recalculate remainPrice
        BigDecimal depositPrice;
        if (existingContract.getDepositPrice() != null) {
            depositPrice = existingContract.getDepositPrice();
        } else {
            depositPrice = BigDecimal.ZERO;
        }
        existingContract.setRemainPrice(existingContract.getTotalPayment().subtract(depositPrice));

        // Update terms if provided
        if (contractDto.getTerms() != null && !contractDto.getTerms().trim().isEmpty()) {
            existingContract.setTerms(contractDto.getTerms());
        }

        // Update uploadedBy if provided
        if (contractDto.getUploadedBy() != null && !contractDto.getUploadedBy().trim().isEmpty()) {
            existingContract.setUploadedBy(contractDto.getUploadedBy());
        }

        existingContract.setUpdatedAt(LocalDateTime.now().format(formatter));

        contractRepository.save(existingContract);
        return mapToDto(existingContract);
    }

    @Override
    public ContractDto updateContractStatus(int id, Contract.ContractStatus status) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        contract.setStatus(status);
        contract.setUpdatedAt(LocalDateTime.now().format(formatter));

        contractRepository.save(contract);
        return mapToDto(contract);
    }

    private ContractDto mapToDto(Contract contract) {
        return ContractDto.builder()
                .contractId(contract.getContractId())
                .contractDate(contract.getContractDate())
                .contractFileUrl(contract.getContractFileUrl())
                .status(contract.getStatus())
                .depositPrice(contract.getDepositPrice())
                .totalPayment(contract.getTotalPayment())
                .remainPrice(contract.getRemainPrice())
                .terms(contract.getTerms())
                .uploadedBy(contract.getUploadedBy())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .orderId(contract.getOrder().getOrderId())
                .build();
    }
}
