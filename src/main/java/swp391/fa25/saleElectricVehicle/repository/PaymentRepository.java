package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findPaymentByPaymentCode(String paymentCode);

    // Find all payments by contract and payment type (may return multiple)
    List<Payment> findByContractAndPaymentType(Contract contract, PaymentType paymentType);
    
    // Find the latest active payment (not CANCELLED or DRAFT) by contract and payment type
    @Query("SELECT p FROM Payment p WHERE p.contract = :contract AND p.paymentType = :paymentType " +
           "AND p.status NOT IN (:cancelledStatus, :draftStatus) ORDER BY p.createdAt DESC")
    List<Payment> findActivePaymentsByContractAndPaymentType(
            @Param("contract") Contract contract, 
            @Param("paymentType") PaymentType paymentType,
            @Param("cancelledStatus") PaymentStatus cancelledStatus,
            @Param("draftStatus") PaymentStatus draftStatus);

    List<Payment> findPaymentsByContract_Order_User_Store(Store contractOrderUserStore);

    Payment findPaymentByPaymentIdAndContract_Order_User_Store(int paymentId, Store contractOrderUserStore);

    // New methods using order.store instead of order.user.store
    List<Payment> findPaymentsByContract_Order_Store(Store contractOrderStore);

    Payment findPaymentByPaymentIdAndContract_Order_Store(int paymentId, Store contractOrderStore);
}
