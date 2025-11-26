package swp391.fa25.saleElectricVehicle.payload.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsDto {

    private String modelName;
    private int modelYear;
    private int seatingCapacity;
    private String bodyType;

    private String colorName;

    // Vehicle information (số khung, số máy, số seri pin)
    private String vin; // số khung
    private String engineNo; // số máy
    private String batteryNo; // số seri pin

    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal discount;
    private BigDecimal totalTax;
    private BigDecimal totalPrice;
}
