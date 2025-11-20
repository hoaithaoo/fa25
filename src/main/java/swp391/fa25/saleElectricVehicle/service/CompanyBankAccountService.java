package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.CompanyBankAccount;
import swp391.fa25.saleElectricVehicle.payload.dto.CompanyBankAccountDto;
import swp391.fa25.saleElectricVehicle.payload.request.companybankaccount.CreateCompanyBankAccountRequest;
import swp391.fa25.saleElectricVehicle.payload.request.companybankaccount.UpdateCompanyBankAccountRequest;

import java.util.List;

public interface CompanyBankAccountService {
    CompanyBankAccount getActiveBankAccount();
    
    CompanyBankAccountDto createCompanyBankAccount(CreateCompanyBankAccountRequest request);
    
    CompanyBankAccountDto getCompanyBankAccountById(int accountId);
    
    List<CompanyBankAccountDto> getAllCompanyBankAccounts();
    
    CompanyBankAccountDto updateCompanyBankAccount(int accountId, UpdateCompanyBankAccountRequest request);
    
    void deleteCompanyBankAccount(int accountId);
}

