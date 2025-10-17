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
    public StoreStockDto createStoreStock(StoreStockDto request) {
        Store store = storeService.getStoreEntityByName(request.getStoreName());
        Model model = modelService.getModelEntityById(request.getModelId());
        Color color = colorService.getColorEntityById(request.getColorId());
        ModelColor modelColor = modelColorService.getModelColorEntityByModelIdAndColorId(model.getModelId(), color.getColorId());

        StoreStock storeStock = storeStockRepository.findByStore_StoreIdAndModelColor_ModelColorId(store.getStoreId(), modelColor.getModelColorId());
        if (storeStock != null) {
            throw new AppException(ErrorCode.STORE_STOCK_EXISTED);
        }

        storeStock = storeStockRepository.save(StoreStock.builder()
                .store(store)
                .modelColor(modelColor)
                .priceOfStore(request.getPriceOfStore())
                .quantity(request.getQuantity())
                .build());

        return mapToDto(storeStock);
    }

    @Override
    public List<StoreStockDto> getAllStoreStock() {
        List<StoreStock> storeStocks = storeStockRepository.findAll();
        return storeStocks.stream().map(this::mapToDto).toList();
    }

//    @Override
//    public StoreStockDto getStoreStockByStoreIdAndModelIdAndColorId(int storeId, int modelId, int colorId) {
//        StoreStock storeStock = storeStockRepository.findByStore_StoreIdAndModelColor_Model_ModelIdAndModelColor_Color_ColorId(storeId, modelId, colorId);
//        if (storeStock == null) {
//            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
//        }
//        return mapToDto(storeStock);
//    }

    // update giá bán của cửa hàng
    @Override
    public StoreStockDto updatePriceOfStore(int stockId, BigDecimal price) {
        StoreStock storeStock = storeStockRepository.findById(stockId).orElse(null);
        if (storeStock == null) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        storeStock.setPriceOfStore(price);
        storeStockRepository.save(storeStock);

        return mapToDto(storeStock);
    }

    // update số lượng tồn kho của cửa hàng
    @Override
    public StoreStockDto updateQuantity(int stockId, int quantity) {
        StoreStock storeStock = storeStockRepository.findById(stockId).orElse(null);
        if (storeStock == null) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }

        if (quantity < 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        storeStock.setQuantity(quantity);
        storeStockRepository.save(storeStock);

        return mapToDto(storeStock);
    }

    @Override
    public void deleteStoreStock(int stockId) {
        StoreStock storeStock = storeStockRepository.findById(stockId).orElse(null);
        if (storeStock == null) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }
        storeStockRepository.delete(storeStock);
    }

    @Override
    public StoreStock getStoreStockEntityById(int stockId) {
        return storeStockRepository.findById(stockId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_STOCK_NOT_FOUND));
    }


    private StoreStockDto mapToDto(StoreStock storeStock) {
        return StoreStockDto.builder()
                .stockId(storeStock.getStockId())
                .storeId(storeStock.getStore().getStoreId())
                .storeName(storeStock.getStore().getStoreName())
                .modelId(storeStock.getModelColor().getModel().getModelId())
                .modelName(storeStock.getModelColor().getModel().getModelName())
                .colorId(storeStock.getModelColor().getColor().getColorId())
                .colorName(storeStock.getModelColor().getColor().getColorName())
                .priceOfStore(storeStock.getPriceOfStore())
                .quantity(storeStock.getQuantity())
                .build();
    }
}
