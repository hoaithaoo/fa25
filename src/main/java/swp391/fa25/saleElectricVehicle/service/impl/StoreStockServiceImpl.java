package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.*;
import swp391.fa25.saleElectricVehicle.payload.request.stock.CreateStoreStockRequest;
import swp391.fa25.saleElectricVehicle.payload.request.stock.UpdatePriceOfStoreRequest;
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

    @Autowired
    private UserService userService;

    @Override
    public StoreStockDto createStoreStock(CreateStoreStockRequest request) {
        User user = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(user.getUserId());
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
    public List<StoreStockDto> getAllStoreStockByStoreId() {
        User user = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(user.getUserId());
        List<StoreStock> storeStocks = storeStockRepository.findStoreStocksByStore_StoreId(store.getStoreId());
        return storeStocks.stream().map(this::mapToDto).toList();
    }

    @Override
    public StoreStock getStoreStockEntityById(int stockId) {
        StoreStock storeStock = storeStockRepository.findById(stockId).orElse(null);
        if (storeStock == null) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }
        return storeStock;
    }

    @Override
    public StoreStock getStoreStockByStoreIdAndModelColorId(int storeId, int modelColorId) {
        StoreStock storeStock = storeStockRepository.findByStore_StoreIdAndModelColor_ModelColorId(storeId, modelColorId);
        if (storeStock == null) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }
        return storeStock;
    }

    @Override
    public StoreStock getStoreStockByStoreIdAndModelColorIdWithLock(int storeId, int modelColorId) {
        StoreStock storeStock = storeStockRepository.findByStore_StoreIdAndModelColor_ModelColorIdWithLock(storeId, modelColorId);
        if (storeStock == null) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }
        return storeStock;
    }

    @Override
    public int getQuantityByModelIdAndColorId(int modelId, int colorId) {
        Model model = modelService.getModelEntityById(modelId);
        Color color = colorService.getColorEntityById(colorId);
        ModelColor modelColor = modelColorService.getModelColorEntityByModelIdAndColorId(model.getModelId(), color.getColorId());
        User user = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(user.getUserId());
        StoreStock storeStock = storeStockRepository.findByStore_StoreIdAndModelColor_ModelColorId(store.getStoreId(), modelColor.getModelColorId());
        return storeStock.getQuantity();
    }

    // update giá bán của cửa hàng
    @Override
    public StoreStockDto updatePriceOfStore(UpdatePriceOfStoreRequest request) {
        User user = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(user.getUserId());
        Model model = modelService.getModelEntityById(request.getModelId());
        Color color = colorService.getColorEntityById(request.getColorId());
        ModelColor modelColor = modelColorService.getModelColorEntityByModelIdAndColorId(model.getModelId(), color.getColorId());
        StoreStock storeStock = storeStockRepository.findByStore_StoreIdAndModelColor_ModelColorId(store.getStoreId(), modelColor.getModelColorId());
        if (storeStock == null) {
            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER);
        }
        storeStock.setPriceOfStore(request.getPrice());
        storeStockRepository.save(storeStock);

        return mapToDto(storeStock);
    }

    // update số lượng tồn kho của cửa hàng
    // chỉ dùng nội bộ khi có đơn hàng hoặc nhập kho
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

//    @Override
//    public void deleteStoreStock(int stockId) {
//        StoreStock storeStock = storeStockRepository.findById(stockId).orElse(null);
//        if (storeStock == null) {
//            throw new AppException(ErrorCode.STORE_STOCK_NOT_FOUND);
//        }
//        storeStockRepository.delete(storeStock);
//    }

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