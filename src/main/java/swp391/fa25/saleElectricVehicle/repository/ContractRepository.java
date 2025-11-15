package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Contract;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    boolean existsByContractFileUrl(String contractFileUrl);
    Contract findContractByContractCode(String contractCode);
    List<Contract> findByOrder_User_Store_StoreId(int storeId);

    // New method using order.store instead of order.user.store
    List<Contract> findByOrder_Store_StoreId(int storeId);
//    boolean existsByContractCode(String contractCode);
//    Contract findByContractFileUrl(String contractFileUrl);
//    List<Contract> findByStatus(Contract.ContractStatus status);
//    Contract findByOrder_OrderId(int orderId); // Tìm contract theo orderId
}
