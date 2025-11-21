package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import swp391.fa25.saleElectricVehicle.config.EvmSignatureConfig;
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
import swp391.fa25.saleElectricVehicle.repository.InventoryTransactionContractRepository;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionContractService;
import swp391.fa25.saleElectricVehicle.service.InventoryTransactionService;
import swp391.fa25.saleElectricVehicle.service.UserService;
import swp391.fa25.saleElectricVehicle.service.StoreService;

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

    @Autowired
    private EvmSignatureConfig evmSignatureConfig;

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

        // Tạo contract, không lưu chữ ký vào database, sẽ lấy từ config khi cần
        InventoryTransactionContract contract = InventoryTransactionContract.builder()
                .contractDate(LocalDate.now())
                .status(InventoryTransactionContractStatus.EVM_SIGNED) // Đã có chữ ký EVM ngay khi tạo
                .uploadedBy(staff.getFullName())
                .evmStaff(staff) // Lưu FK relationship với User
                .createdAt(LocalDateTime.now())
                .inventoryTransaction(transaction)
                .build();

        // Save to get contractId
        contractRepository.save(contract);
        contract.setContractCode("ITC" + String.format("%06d", contract.getContractId()));
        InventoryTransactionContract saved = contractRepository.save(contract);

        // Update InventoryTransaction status to EVM_SIGNED when contract is created
        inventoryTransactionService.updateStatusToEvmSigned(inventoryId);

        return mapToDto(saved);
    }

    @Override
    public String getContractHtml(int inventoryId) {
        // Lấy chữ ký từ config, không lưu vào database
        String evmSignatureUrl = evmSignatureConfig.getDefaultSignatureUrl();
        if (evmSignatureUrl == null || evmSignatureUrl.trim().isEmpty()) {
            throw new AppException(ErrorCode.EVM_SIGNATURE_NOT_CONFIGURED);
        }

        // Lấy thông tin contract để có thông tin EVM staff
        InventoryTransactionContract contract = getContractEntityByInventoryId(inventoryId);

        // Generate HTML với chữ ký từ config và thông tin EVM staff
        // evmStaff đã được load qua FK relationship
        return generateContractHtml(inventoryId, evmSignatureUrl, contract.getEvmStaff());
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

    private String generateContractHtml(int inventoryId, String evmSignatureUrl, User evmStaff) {
        InventoryTransactionDto transaction = inventoryTransactionService.getInventoryTransactionById(inventoryId);

        StoreDto storeDto = storeService.getStoreById(transaction.getStoreId());

        // Prepare context for Thymeleaf
        Context context = new Context();
        context.setVariable("transaction", transaction);
        context.setVariable("evmStaff", evmStaff);
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
                .status(contract.getStatus() != null ? contract.getStatus().name() : null)
                .uploadedBy(contract.getUploadedBy())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .inventoryId(contract.getInventoryTransaction().getInventoryId())
                .build();
    }
}

