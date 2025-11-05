package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.OrderStatus;
import swp391.fa25.saleElectricVehicle.payload.response.order.GetOrderResponse;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Find by customer
    List<Order> findByCustomer_CustomerId(int customerId);

    // Find by staff
    List<Order> findByUser_UserId(int staffId);

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
}