package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Find by customer
    List<Order> findByCustomer_CustomerId(int customerId);

    // Find by staff
    List<Order> findByUser_UserId(int staffId);

    // Find by staff and date range (for monthly orders)
    List<Order> findByUser_UserIdAndOrderDateBetween(int staffId, LocalDateTime startDate, LocalDateTime endDate);

    // Find by status
    List<Order> findByStatus(OrderStatus status);

    // Find by date range
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find by customer phone (for search)
    @Query("SELECT o FROM Order o WHERE o.customer.phone = :phone")
    List<Order> findByCustomerPhone(@Param("phone") String phone);

    // Count orders by status
    long countByStatus(OrderStatus status);

    // Find recent orders
    List<Order> findTop10ByOrderByOrderDateDesc();

    Order findOrderByUser_Store_StoreIdAndOrderId(int storeId, int orderId);

    List<Order> findOrdersByUser_Store_StoreId(int storeId);

    List<Order> findByCustomer_CustomerIdAndUser_Store_StoreId(int customerId, int storeId);

    // New methods using order.store instead of order.user.store
    Order findByStore_StoreIdAndOrderId(int storeId, int orderId);

    List<Order> findByStore_StoreId(int storeId);

    List<Order> findByCustomer_CustomerIdAndStore_StoreId(int customerId, int storeId);

    // Find expired DRAFT orders
    List<Order> findByStatusAndOrderDateBefore(OrderStatus status, LocalDateTime date);

    // Find expired CONFIRMED orders
    List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime date);

    // Find orders by store, status and date range (for revenue calculation)
//    @Query("SELECT o FROM Order o WHERE o.store.storeId = :storeId " +
//           "AND o.status = :status " +
//           "AND o.orderDate >= :startDate AND o.orderDate < :endDate")
    List<Order> findByStore_StoreIdAndStatusAndOrderDateBetween(
            int storeId, OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // Find orders by store and user (for staff filtering)
    List<Order> findByStore_StoreIdAndUser_UserId(int storeId, int userId);

    // Find order by store, user and orderId (for staff filtering)
    Order findByStore_StoreIdAndUser_UserIdAndOrderId(int storeId, int userId, int orderId);
}