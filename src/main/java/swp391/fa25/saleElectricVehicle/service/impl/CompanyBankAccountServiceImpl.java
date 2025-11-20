package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.fa25.saleElectricVehicle.entity.CompanyBankAccount;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.CompanyBankAccountDto;
import swp391.fa25.saleElectricVehicle.payload.request.companybankaccount.CreateCompanyBankAccountRequest;
import swp391.fa25.saleElectricVehicle.payload.request.companybankaccount.UpdateCompanyBankAccountRequest;
import swp391.fa25.saleElectricVehicle.repository.CompanyBankAccountRepository;
import swp391.fa25.saleElectricVehicle.service.CompanyBankAccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyBankAccountServiceImpl implements CompanyBankAccountService {

    @Autowired
    private CompanyBankAccountRepository companyBankAccountRepository;

    @Override
    public CompanyBankAccount getActiveBankAccount() {
        return companyBankAccountRepository
                .findByIsActiveTrue()
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_BANK_ACCOUNT_NOT_FOUND));
    }

    @Override
    @Transactional
    public CompanyBankAccountDto createCompanyBankAccount(CreateCompanyBankAccountRequest request) {
        // Nếu set account mới thành active, set tất cả account khác thành inactive
        if (request.getIsActive()) {
            List<CompanyBankAccount> activeAccounts = companyBankAccountRepository.findAll()
                    .stream()
                    .filter(CompanyBankAccount::getIsActive)
                    .collect(Collectors.toList());
            
            for (CompanyBankAccount account : activeAccounts) {
                account.setIsActive(false);
                account.setUpdatedAt(LocalDateTime.now());
                companyBankAccountRepository.save(account);
            }
        }

        CompanyBankAccount account = CompanyBankAccount.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolderName(request.getAccountHolderName())
                .isActive(request.getIsActive())
                .createdAt(LocalDateTime.now())
                .build();

        CompanyBankAccount saved = companyBankAccountRepository.save(account);
        return mapToDto(saved);
    }

    @Override
    public CompanyBankAccountDto getCompanyBankAccountById(int accountId) {
        CompanyBankAccount account = companyBankAccountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_BANK_ACCOUNT_NOT_FOUND));
        return mapToDto(account);
    }

    @Override
    public List<CompanyBankAccountDto> getAllCompanyBankAccounts() {
        return companyBankAccountRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyBankAccountDto updateCompanyBankAccount(int accountId, UpdateCompanyBankAccountRequest request) {
        CompanyBankAccount account = companyBankAccountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_BANK_ACCOUNT_NOT_FOUND));

        // Nếu set account này thành active, set tất cả account khác thành inactive
        if (request.getIsActive() && !account.getIsActive()) {
            List<CompanyBankAccount> activeAccounts = companyBankAccountRepository.findAll()
                    .stream()
                    .filter(a -> a.getAccountId() != accountId && a.getIsActive())
                    .collect(Collectors.toList());
            
            for (CompanyBankAccount activeAccount : activeAccounts) {
                activeAccount.setIsActive(false);
                activeAccount.setUpdatedAt(LocalDateTime.now());
                companyBankAccountRepository.save(activeAccount);
            }
        }

        account.setBankName(request.getBankName());
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountHolderName(request.getAccountHolderName());
        account.setIsActive(request.getIsActive());
        account.setUpdatedAt(LocalDateTime.now());

        CompanyBankAccount saved = companyBankAccountRepository.save(account);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteCompanyBankAccount(int accountId) {
        CompanyBankAccount account = companyBankAccountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_BANK_ACCOUNT_NOT_FOUND));
        
        // Không cho phép xóa account đang active
        if (account.getIsActive()) {
            throw new AppException(ErrorCode.COMPANY_BANK_ACCOUNT_CANNOT_DELETE_ACTIVE);
        }
        
        companyBankAccountRepository.delete(account);
    }

    private CompanyBankAccountDto mapToDto(CompanyBankAccount account) {
        return CompanyBankAccountDto.builder()
                .accountId(account.getAccountId())
                .bankName(account.getBankName())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .isActive(account.getIsActive())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

