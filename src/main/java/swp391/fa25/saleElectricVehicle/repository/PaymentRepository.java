package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Order;
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

    // Find all payments by order and payment type (may return multiple)
    List<Payment> findByOrderAndPaymentType(Order order, PaymentType paymentType);
    
    // Find the latest active payment (not CANCELLED or DRAFT) by order and payment type
    @Query("SELECT p FROM Payment p WHERE p.order = :order AND p.paymentType = :paymentType " +
           "AND p.status NOT IN (:cancelledStatus, :draftStatus) ORDER BY p.createdAt DESC")
    List<Payment> findActivePaymentsByOrderAndPaymentType(
            @Param("order") Order order, 
            @Param("paymentType") PaymentType paymentType,
            @Param("cancelledStatus") PaymentStatus cancelledStatus,
            @Param("draftStatus") PaymentStatus draftStatus);

    // Find payments by order
    List<Payment> findByOrder_OrderId(int orderId);

    // Find payments by store
    List<Payment> findByOrder_Store(Store store);

    Payment findPaymentByPaymentIdAndOrder_Store(int paymentId, Store store);

    // Find payments by store and user (for staff filtering)
    List<Payment> findByOrder_Store_StoreIdAndOrder_User_UserId(int storeId, int userId);

    // Find payment by store, user and paymentId (for staff filtering)
    Payment findByOrder_Store_StoreIdAndOrder_User_UserIdAndPaymentId(int storeId, int userId, int paymentId);
}
