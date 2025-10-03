package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelDto {
    private int modelId;
    private String modelName;
    private int modelYear;
    private BigDecimal batteryCapacity;
    private BigDecimal range;
    private BigDecimal powerHp;
    private BigDecimal torqueNm;
    private BigDecimal acceleration;
    private int seatingCapacity;
    private BigDecimal price;
    private String bodyType;
    private String description;
}
