package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.BodyType;

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
//    private BigDecimal price;
    private BodyType bodyType;
    private String description;
}
