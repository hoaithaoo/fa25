package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.payload.dto.UserDto;
import swp391.fa25.saleElectricVehicle.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    @Autowired
    StoreService storeService;

    @PostMapping("/create")
    public StoreDto createStore(StoreDto storeDto) {
        return storeService.createStore(storeDto);
    }

    @GetMapping("/{storeId}")
    public StoreDto getStoreById(int storeId) {
        return storeService.findStoreById(storeId);
    }

    @GetMapping("/all")
    public List<StoreDto> getAllStores() {
        return storeService.findAllStores();
    }
}
