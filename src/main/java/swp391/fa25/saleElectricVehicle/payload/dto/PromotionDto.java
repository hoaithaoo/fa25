package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PromotionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionDto {
    private int promotionId;
    private String promotionName;
    private String description;
    private PromotionType promotionType;
    private BigDecimal amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private int modelId;
    private String modelName;
    private Integer storeId;
    private String storeName;
    private LocalDateTime createdAt;
}
