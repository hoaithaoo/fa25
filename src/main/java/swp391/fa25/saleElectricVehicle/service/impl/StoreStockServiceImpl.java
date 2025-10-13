package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.*;
import swp391.fa25.saleElectricVehicle.repository.StoreStockRepository;
import swp391.fa25.saleElectricVehicle.service.*;

import java.math.BigDecimal;
import java.util.List;

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
        ModelDto model = modelService.getModelByName(createStoreStock.getModelName());
        if (model == null) {
            throw new AppException(ErrorCode.MODEL_NOT_FOUND);
        }

        // không tìm thấy color
        Color color = colorService.getColorEntityByName(createStoreStock.getColorName());
        if (color == null) {
            throw new AppException(ErrorCode.COLOR_NOT_EXIST);
        }

        // model và color không tồn tại
        ModelColor modelColor = modelColorService.getModelColor(model.getModelId(), color.getColorId());
        if (modelColor == null) {
            throw new AppException(ErrorCode.MODEL_COLOR_NOT_EXIST);
        }

        StoreStock storeStock = StoreStock.builder()
                        .store(store)
                        .modelColor(modelColor)
                        .priceOfStore(createStoreStock.getPriceOfStore())
                        .quantity(createStoreStock.getQuantity())
                        .build();

        StoreStock saved = storeStockRepository.save(storeStock);

        return mapToDto(saved);
    }

    @Override
    public StoreStockDto getStoreStockById(int stockId) {
        StoreStock storeStock = storeStockRepository.findById(stockId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_STOCK_NOT_FOUND));
        return mapToDto(storeStock);
    }

    @Override
    public List<StoreStockDto> getAllStoreStocks() {
        return storeStockRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public StoreStockDto updateStoreStock(int stockId, StoreStockDto storeStockDto) {
        StoreStock storeStock = storeStockRepository.findById(stockId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_STOCK_NOT_FOUND));

        // Update price
        if (storeStockDto.getPriceOfStore() != null &&
                storeStockDto.getPriceOfStore().compareTo(BigDecimal.ZERO) > 0) {
            storeStock.setPriceOfStore(storeStockDto.getPriceOfStore());
        }

        // Update quantity
        if (storeStockDto.getQuantity() >= 0) {
            storeStock.setQuantity(storeStockDto.getQuantity());
        }

        StoreStock saved = storeStockRepository.save(storeStock);
        return mapToDto(saved);
    }

    @Override
    public void deleteStoreStock(int stockId) {
        StoreStock storeStock = storeStockRepository.findById(stockId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_STOCK_NOT_FOUND));
        storeStockRepository.delete(storeStock);
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
