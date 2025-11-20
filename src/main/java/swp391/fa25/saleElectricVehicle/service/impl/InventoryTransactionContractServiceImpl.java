package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransaction;
import swp391.fa25.saleElectricVehicle.entity.InventoryTransactionContract;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionContractStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.InventoryTransactionStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionContractDto;
import swp391.fa25.saleElectricVehicle.payload.dto.InventoryTransactionDto;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.payload.request.inventorytransactioncontract.SignInventoryTransactionContractRequest;
import swp391.fa25.saleElectricVehicle.repository.InventoryTransactionContractRepository;
import swp391.fa25.saleElectricVehicle.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InventoryTransactionContractServiceImpl implements InventoryTransactionContractService {

    @Autowired
    private InventoryTransactionContractRepository contractRepository;

    @Autowired
    private InventoryTransactionService inventoryTransactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    @Transactional
    public InventoryTransactionContractDto createDraftContract(int inventoryId) {
        InventoryTransaction transaction = inventoryTransactionService.getInventoryTransactionEntityById(inventoryId);

        // Validate status = CONFIRMED
        if (transaction.getStatus() != InventoryTransactionStatus.CONFIRMED) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_CREATE_CONTRACT);
        }

        // Check if contract already exists
        Optional<InventoryTransactionContract> existingContract = contractRepository.findByInventoryTransaction_InventoryId(inventoryId);
        if (existingContract.isPresent()) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CONTRACT_ALREADY_EXISTS);
        }

        User staff = userService.getCurrentUserEntity();

        // Create draft contract
        InventoryTransactionContract contract = InventoryTransactionContract.builder()
                .contractDate(LocalDate.now())
                .status(InventoryTransactionContractStatus.DRAFT)
                .uploadedBy(staff.getFullName())
                .createdAt(LocalDateTime.now())
                .inventoryTransaction(transaction)
                .build();

        // Save to get contractId
        contractRepository.save(contract);
        contract.setContractCode("ITC" + String.format("%06d", contract.getContractId()));
        InventoryTransactionContract saved = contractRepository.save(contract);

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public InventoryTransactionContractDto signContract(int inventoryId, SignInventoryTransactionContractRequest request) {
        InventoryTransactionContract contract = getContractEntityByInventoryId(inventoryId);

        // Validate status = DRAFT
        if (contract.getStatus() != InventoryTransactionContractStatus.DRAFT) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_SIGN_CONTRACT);
        }

        // Lưu URL ảnh chữ ký EVM (không upload HTML)
        contract.setEvmSignatureUrl(request.getEvmSignatureImageUrl());
        contract.setStatus(InventoryTransactionContractStatus.EVM_SIGNED);
        contract.setUpdatedAt(LocalDateTime.now());

        InventoryTransactionContract saved = contractRepository.save(contract);
        return mapToDto(saved);
    }

    @Override
    public String getContractHtml(int inventoryId) {
        InventoryTransactionContract contract = getContractEntityByInventoryId(inventoryId);

        // If contract is EVM_SIGNED, generate HTML with EVM signature
        if (contract.getStatus() == InventoryTransactionContractStatus.EVM_SIGNED) {
            return generateContractHtml(inventoryId, contract.getEvmSignatureUrl());
        }

        // Otherwise, generate HTML without signature (for draft)
        return generateContractHtml(inventoryId, null);
    }

    @Override
    @Transactional
    public InventoryTransactionContractDto uploadSignedContract(int inventoryId, String fileUrl) {
        InventoryTransactionContract contract = getContractEntityByInventoryId(inventoryId);

        // Validate status = EVM_SIGNED
        if (contract.getStatus() != InventoryTransactionContractStatus.EVM_SIGNED) {
            throw new AppException(ErrorCode.INVENTORY_TRANSACTION_CANNOT_UPLOAD_CONTRACT);
        }

        // Validate fileUrl unique
        if (contractRepository.existsByContractFileUrl(fileUrl)) {
            throw new AppException(ErrorCode.CONTRACT_FILE_URL_EXISTED);
        }

        User staff = userService.getCurrentUserEntity();

        // Update contract
        contract.setContractFileUrl(fileUrl);
        contract.setStatus(InventoryTransactionContractStatus.SIGNED);
        contract.setUploadedBy(staff.getFullName());
        contract.setUpdatedAt(LocalDateTime.now());

        InventoryTransactionContract saved = contractRepository.save(contract);

        // Update InventoryTransaction status to CONTRACT_SIGNED (service gọi service)
        inventoryTransactionService.updateStatusToContractSigned(inventoryId);

        return mapToDto(saved);
    }

    @Override
    public InventoryTransactionContractDto getContractByInventoryId(int inventoryId) {
        InventoryTransactionContract contract = getContractEntityByInventoryId(inventoryId);
        return mapToDto(contract);
    }

    @Override
    public InventoryTransactionContract getContractEntityByInventoryId(int inventoryId) {
        return contractRepository.findByInventoryTransaction_InventoryId(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_TRANSACTION_CONTRACT_NOT_FOUND));
    }

    private String generateContractHtml(int inventoryId, String evmSignatureUrl) {
        InventoryTransactionDto transaction = inventoryTransactionService.getInventoryTransactionById(inventoryId);
        StoreDto storeDto = storeService.getStoreById(transaction.getStoreId());

        // Prepare context for Thymeleaf
        Context context = new Context();
        context.setVariable("transaction", transaction);
        context.setVariable("store", storeDto);
        context.setVariable("evmSignatureUrl", evmSignatureUrl);

        // Render HTML from template
        return templateEngine.process("inventory-contract", context);
    }

    private InventoryTransactionContractDto mapToDto(InventoryTransactionContract contract) {
        return InventoryTransactionContractDto.builder()
                .contractId(contract.getContractId())
                .contractCode(contract.getContractCode())
                .contractDate(contract.getContractDate())
                .contractFileUrl(contract.getContractFileUrl())
                .evmSignatureUrl(contract.getEvmSignatureUrl())
                .status(contract.getStatus() != null ? contract.getStatus().name() : null)
                .uploadedBy(contract.getUploadedBy())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .inventoryId(contract.getInventoryTransaction().getInventoryId())
                .build();
    }
}

