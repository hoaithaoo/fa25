package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    boolean existsByContractFileUrl(String contractFileUrl);
    Contract findContractByContractCode(String contractCode);
    List<Contract> findByOrder_User_Store_StoreId(int storeId);

    // New method using order.store instead of order.user.store
    List<Contract> findByOrder_Store_StoreId(int storeId);

    // Find contracts by store and user (for staff filtering)
    List<Contract> findByOrder_Store_StoreIdAndOrder_User_UserId(int storeId, int userId);

    // Find contract by store, user and contractId (for staff filtering)
    Contract findByOrder_Store_StoreIdAndOrder_User_UserIdAndContractId(int storeId, int userId, int contractId);

    // Find contract by order and contractType
    Optional<Contract> findByOrder_OrderIdAndContractType(int orderId, ContractType contractType);

    // Check if contract exists by order and contractType
    boolean existsByOrder_OrderIdAndContractType(int orderId, ContractType contractType);
//    boolean existsByContractCode(String contractCode);
//    Contract findByContractFileUrl(String contractFileUrl);
//    List<Contract> findByStatus(Contract.ContractStatus status);
//    Contract findByOrder_OrderId(int orderId); // Tìm contract theo orderId
}
