package swp391.fa25.saleElectricVehicle.payload.request.model;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateModelRequest {
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
