package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findPaymentByPaymentCode(String paymentCode);

    Optional<Payment> findByContractAndPaymentType(Contract contract, PaymentType paymentType);

    List<Payment> findPaymentsByContract_Order_User_Store(Store contractOrderUserStore);

    Payment findPaymentByPaymentIdAndContract_Order_User_Store(int paymentId, Store contractOrderUserStore);

    // New methods using order.store instead of order.user.store
    List<Payment> findPaymentsByContract_Order_Store(Store contractOrderStore);

    Payment findPaymentByPaymentIdAndContract_Order_Store(int paymentId, Store contractOrderStore);
}
