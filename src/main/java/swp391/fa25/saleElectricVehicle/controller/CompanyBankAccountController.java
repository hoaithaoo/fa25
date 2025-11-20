package swp391.fa25.saleElectricVehicle.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.dto.CompanyBankAccountDto;
import swp391.fa25.saleElectricVehicle.payload.request.companybankaccount.CreateCompanyBankAccountRequest;
import swp391.fa25.saleElectricVehicle.payload.request.companybankaccount.UpdateCompanyBankAccountRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.CompanyBankAccountService;

import java.util.List;

@RestController
@RequestMapping("/company-bank-accounts")
public class CompanyBankAccountController {

    @Autowired
    private CompanyBankAccountService companyBankAccountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CompanyBankAccountDto>> createCompanyBankAccount(
            @Valid @RequestBody CreateCompanyBankAccountRequest request) {

        CompanyBankAccountDto created = companyBankAccountService.createCompanyBankAccount(request);

        ApiResponse<CompanyBankAccountDto> response = ApiResponse.<CompanyBankAccountDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tài khoản ngân hàng đã được tạo thành công")
                .data(created)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<CompanyBankAccountDto>> getCompanyBankAccountById(
            @PathVariable int accountId) {

        CompanyBankAccountDto dto = companyBankAccountService.getCompanyBankAccountById(accountId);

        ApiResponse<CompanyBankAccountDto> response = ApiResponse.<CompanyBankAccountDto>builder()
                .code(HttpStatus.OK.value())
                .message("Tài khoản ngân hàng đã được lấy thành công")
                .data(dto)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CompanyBankAccountDto>>> getAllCompanyBankAccounts() {

        List<CompanyBankAccountDto> dtos = companyBankAccountService.getAllCompanyBankAccounts();

        ApiResponse<List<CompanyBankAccountDto>> response = ApiResponse.<List<CompanyBankAccountDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Danh sách tài khoản ngân hàng đã được lấy thành công")
                .data(dtos)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{accountId}")
    public ResponseEntity<ApiResponse<CompanyBankAccountDto>> updateCompanyBankAccount(
            @PathVariable int accountId,
            @Valid @RequestBody UpdateCompanyBankAccountRequest request) {

        CompanyBankAccountDto updated = companyBankAccountService.updateCompanyBankAccount(accountId, request);

        ApiResponse<CompanyBankAccountDto> response = ApiResponse.<CompanyBankAccountDto>builder()
                .code(HttpStatus.OK.value())
                .message("Tài khoản ngân hàng đã được cập nhật thành công")
                .data(updated)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompanyBankAccount(@PathVariable int accountId) {

        companyBankAccountService.deleteCompanyBankAccount(accountId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Tài khoản ngân hàng đã được xóa thành công")
                .build();

        return ResponseEntity.ok(response);
    }
}

