package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Contract;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    boolean existsByContractFileUrl(String contractFileUrl);
    Contract findContractByContractCode(String contractCode);
//    boolean existsByContractCode(String contractCode);
//    Contract findByContractFileUrl(String contractFileUrl);
//    List<Contract> findByStatus(Contract.ContractStatus status);
//    Contract findByOrder_OrderId(int orderId); // Tìm contract theo orderId
}
