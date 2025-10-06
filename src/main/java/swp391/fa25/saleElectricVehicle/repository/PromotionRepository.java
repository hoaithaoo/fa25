package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Promotion;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion,Integer> {
    List<Promotion> findByIsActiveTrue();
    List<Promotion> findByPromotionNameContainingIgnoreCase(String promotionName);
    boolean existsByPromotionNameIgnoreCase(String promotionName);
}
