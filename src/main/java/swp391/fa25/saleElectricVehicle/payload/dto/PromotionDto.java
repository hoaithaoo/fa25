package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

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
    private String promotionType;
    private BigDecimal amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private int modelId;
    private int storeId;
}
