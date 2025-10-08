package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.*;
import swp391.fa25.saleElectricVehicle.repository.StoreStockRepository;
import swp391.fa25.saleElectricVehicle.service.*;

@Service
public class StoreStockServiceImpl implements StoreStockService {

    @Autowired
    StoreStockRepository storeStockRepository;

    @Autowired
    StoreService storeService;

    @Autowired
    ModelService modelService;

    @Autowired
    ColorService colorService;

    @Autowired
    ModelColorService modelColorService;

    @Override
    public StoreStockDto createStoreStock(StoreStockDto createStoreStock) {
        // không tìm thấy store
        Store store = storeService.getStoreEntityByName(createStoreStock.getStoreName());
        if (store == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXIST);
        }

        // không tìm thấy model
        Model model = modelService.getModelEntityByName(createStoreStock.getModelName());
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }

        // không tìm thấy color
        Color color = colorService.getColorEntityByName(createStoreStock.getColorName());
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }

        // model và color không tồn tại
        if (modelColorService.getModelColor(model.getModelId(), color.getColorId()) == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }
        ModelColor modelColor = ModelColor.builder()
                .modelColorId(model.getModelId())
                .model(Model.builder()
                        .modelId(model.getModelId())
                        .modelName(model.getModelName())
                        .modelYear(model.getModelYear())
                        .batteryCapacity(model.getBatteryCapacity())
                        .range(model.getRange())
                        .powerHp(model.getPowerHp())
                        .torqueNm(model.getTorqueNm())
                        .acceleration(model.getAcceleration())
                        .seatingCapacity(model.getSeatingCapacity())
                        .price(model.getPrice())
                        .bodyType(model.getBodyType())
                        .description(model.getDescription())
                        .build())
                .color(Color.builder()
                        .colorId(color.getColorId())
                        .colorName(color.getColorName())
                        .build())
                .build();

        StoreStock storeStock = StoreStock.builder()
                        .store(store)
                        .modelColor(modelColor)
                        .priceOfStore(createStoreStock.getPriceOfStore())
                        .quantity(createStoreStock.getQuantity())
                        .build();

        storeStockRepository.save(storeStock);

        return mapToDto(storeStock);
    }

    private StoreStockDto mapToDto(StoreStock storeStock) {
        return StoreStockDto.builder()
                .stockId(storeStock.getStockId())
                .storeName(storeStock.getStore().getStoreName())
                .modelName(storeStock.getModelColor().getModel().getModelName())
                .colorName(storeStock.getModelColor().getColor().getColorName())
                .priceOfStore(storeStock.getPriceOfStore())
                .quantity(storeStock.getQuantity())
                .build();
    }
}
