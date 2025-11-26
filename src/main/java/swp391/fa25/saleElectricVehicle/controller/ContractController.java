package swp391.fa25.saleElectricVehicle.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import swp391.fa25.saleElectricVehicle.payload.dto.ContractDto;
import swp391.fa25.saleElectricVehicle.payload.dto.CustomerDto;
import swp391.fa25.saleElectricVehicle.payload.dto.OrderDto;
import swp391.fa25.saleElectricVehicle.payload.dto.StoreDto;
import swp391.fa25.saleElectricVehicle.payload.request.contract.CreateContractRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    SpringTemplateEngine templateEngine;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private StoreService storeService;

    // Tạo hợp đồng (đặt cọc hoặc mua bán)
    @PostMapping("/contracts")
    public ResponseEntity<Map<String, Object>> createContract(@RequestBody CreateContractRequest request) {
        ContractDto created = contractService.createContract(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Contract created successfully");
        response.put("contractId", created.getContractId());
        response.put("viewUrl", "/api/contracts/" + created.getContractId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Xem hợp đồng dưới dạng HTML
    @GetMapping("/{id}")
    public ResponseEntity<String> getContractById(@PathVariable int id) {
        // Lấy thông tin hợp đồng
        ContractDto contract = contractService.getContractById(id);
        OrderDto order = orderService.getOrderDtoById(contract.getOrderId());
        CustomerDto customer = customerService.getCustomerById(order.getCustomerId());
        StoreDto store = storeService.getStoreById(order.getStoreId());

        // Chuẩn bị model data cho temp-late engine
        Context context = new Context();  // Với Thymeleaf
        context.setVariable("contract", contract); // Đặt biến "contract" cho template
        context.setVariable("order", order);
        context.setVariable("customer", customer);
        context.setVariable("store", store);

        // Render ra HTML dựa trên template contract.html
        String htmlContent = templateEngine.process("contract", context); // "contract" là tên file contract.html

        // Trả về dạng HTML
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }


    // Upload hợp đồng đã ký
    @PostMapping(
            value = "/{contractId}/upload-signed",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<String>> uploadSignedContract(
            @PathVariable int contractId,
            @Parameter(description = "File hợp đồng đã ký", required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file) {
        contractService.getContractById(contractId);
        String fileUrl = cloudinaryService.uploadFile(file, "contracts");
        contractService.addFileUrlContract(contractId, fileUrl);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("File uploaded successfully")
                .data(fileUrl)
                .build();
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<ContractDto>> getContractById(@PathVariable int id) {
//        ContractDto contractDto = contractService.getContractById(id);
//        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Get contract successfully")
//                .data(contractDto)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/file")
//    public ResponseEntity<ApiResponse<ContractDto>> getContractByFileUrl(@RequestParam String fileUrl) {
//        ContractDto contractDto = contractService.getContractByFileUrl(fileUrl);
//        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Get contract successfully")
//                .data(contractDto)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ContractDto>>> getAllContracts() {
        List<ContractDto> contracts = contractService.getAllContracts();
        ApiResponse<List<ContractDto>> response = ApiResponse.<List<ContractDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Contracts fetched successfully")
                .data(contracts)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<ContractDto>> getContractDetail(@PathVariable int id) {
        ContractDto contractDetail = contractService.getContractDetailById(id);
        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get contract detail successfully")
                .data(contractDetail)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<String>>> getContractStatus() {
        List<String> statuses = Arrays.stream(ContractStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Contract status retrieved successfully")
                .data(statuses)
                .build();
        return ResponseEntity.ok(response);
    }
//
//    @GetMapping("/status/{status}")
//    public ResponseEntity<ApiResponse<List<ContractDto>>> getContractsByStatus(@PathVariable Contract.ContractStatus status) {
//        List<ContractDto> contracts = contractService.getContractsByStatus(status);
//        ApiResponse<List<ContractDto>> response = ApiResponse.<List<ContractDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Contracts fetched successfully by status")
//                .data(contracts)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> deleteContract(@PathVariable int id) {
//        contractService.deleteContractById(id);
//        ApiResponse<Void> response = ApiResponse.<Void>builder()
//                .code(HttpStatus.OK.value())
//                .message("Delete contract successfully")
//                .data(null)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<ApiResponse<ContractDto>> updateContract(@PathVariable int id, @RequestBody ContractDto contractDto) {
//        ContractDto updatedContract = contractService.updateContract(id, contractDto);
//        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Update contract successfully")
//                .data(updatedContract)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<ApiResponse<ContractDto>> updateContractStatus(
//            @PathVariable int id,
//            @RequestParam Contract.ContractStatus status) {
//        ContractDto updatedContract = contractService.updateContractStatus(id, status);
//        ApiResponse<ContractDto> response = ApiResponse.<ContractDto>builder()
//                .code(HttpStatus.OK.value())
//                .message("Update contract status successfully")
//                .data(updatedContract)
//                .build();
//        return ResponseEntity.ok(response);
//    }
}
