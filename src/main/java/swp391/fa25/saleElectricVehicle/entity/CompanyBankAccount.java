package swp391.fa25.saleElectricVehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_bank_accounts")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyBankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;
    
    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String bankName; // Tên ngân hàng
    
    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String accountNumber; // Số tài khoản
    
    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String accountHolderName; // Tên chủ tài khoản
    
    @Column(nullable = false)
    private Boolean isActive; // Tài khoản đang sử dụng
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
}

