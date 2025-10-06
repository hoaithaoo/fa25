package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.OrderDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Find by Order
    List<OrderDetail> findByOrder_OrderId(int orderId);

    // Find by StoreStock (for inventory tracking)
    List<OrderDetail> findByStoreStock_StockId(int storeStockId);

    // Find by Promotion
    List<OrderDetail> findByPromotion_PromotionId(int promotionId);

    // Find by Model (through StoreStock -> ModelColor -> Model)
    @Query("SELECT od FROM OrderDetail od WHERE od.storeStock.modelColor.model.modelId = :modelId")
    List<OrderDetail> findByModelId(@Param("modelId") int modelId);

    // Find by Color (through StoreStock -> ModelColor -> Color)
    @Query("SELECT od FROM OrderDetail od WHERE od.storeStock.modelColor.color.colorId = :colorId")
    List<OrderDetail> findByColorId(@Param("colorId") int colorId);

    // Find by Store (through StoreStock -> Store)
    @Query("SELECT od FROM OrderDetail od WHERE od.storeStock.store.storeId = :storeId")
    List<OrderDetail> findByStoreId(@Param("storeId") int storeId);

    // Find by date range
    List<OrderDetail> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Analytics queries
    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.storeStock.modelColor.model.modelId = :modelId")
    Integer getTotalQuantitySoldByModel(@Param("modelId") int modelId);

    @Query("SELECT SUM(od.totalPrice) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal getTotalAmountByOrder(@Param("orderId") int orderId);

    // Electric Vehicle specific analytics
    @Query("SELECT SUM(od.vatAmount) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal getTotalVATByOrder(@Param("orderId") int orderId);

    @Query("SELECT SUM(od.licensePlateFee) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal getTotalLicenseFeeByOrder(@Param("orderId") int orderId);

    @Query("SELECT SUM(od.registrationFee) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal getTotalRegistrationFeeByOrder(@Param("orderId") int orderId);

    @Query("SELECT SUM(od.discountAmount) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal getTotalDiscountByOrder(@Param("orderId") int orderId);

    // Top selling models by revenue
    @Query("SELECT od.storeStock.modelColor.model.modelId, SUM(od.totalPrice) as totalRevenue " +
            "FROM OrderDetail od " +
            "GROUP BY od.storeStock.modelColor.model.modelId " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getTopSellingModelsByRevenue();

    // Count by order
    long countByOrder_OrderId(int orderId);

    // Average order value
    @Query("SELECT AVG(od.totalPrice) FROM OrderDetail od")
    BigDecimal getAverageOrderDetailValue();

    // Find high value order details
    List<OrderDetail> findByTotalPriceGreaterThan(BigDecimal amount);

    // Find by quantity range
    List<OrderDetail> findByQuantityBetween(int minQuantity, int maxQuantity);
}