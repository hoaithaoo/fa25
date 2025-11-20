package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.CompanyBankAccount;

import java.util.Optional;

@Repository
public interface CompanyBankAccountRepository extends JpaRepository<CompanyBankAccount, Integer> {
    Optional<CompanyBankAccount> findByIsActiveTrue();
}

