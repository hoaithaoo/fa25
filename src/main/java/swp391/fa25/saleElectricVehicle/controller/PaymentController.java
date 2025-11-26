package swp391.fa25.saleElectricVehicle.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetPaymentResponse;
import swp391.fa25.saleElectricVehicle.service.PaymentService;
import swp391.fa25.saleElectricVehicle.service.VNPayService;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentType;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentMethod;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentGateway;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VNPayService vnpayService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createPayment(@RequestBody CreatePaymentRequest request, HttpServletRequest req) {
        GetPaymentResponse payment = paymentService.createDraftPayment(request);
        String paymentUrl = vnpayService.buildPaymentUrl(payment.getPaymentId(), req);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.CREATED.value())
                .message("VNPay payment URL generated successfully")
                .data(paymentUrl)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        String paymentUrl = vnpayService.buildPaymentUrl(
//                CreatePaymentUrlRequest.builder()
//                        .paymentId(payment.getPaymentId())
//                        .build()
//        );
//        ApiResponse<GetPaymentResponse> response = ApiResponse.<GetPaymentResponse>builder()
//                .code(HttpStatus.CREATED.value())
//                .message("Draft payment created successfully")
//                .data(payment)
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // chỉ được xem payment của cửa hàng mình
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<GetPaymentResponse>> getPaymentById(@PathVariable int paymentId) {
        GetPaymentResponse payment = paymentService.getPaymentById(paymentId);
        ApiResponse<GetPaymentResponse> response = ApiResponse.<GetPaymentResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Payment retrieved successfully")
                .data(payment)
                .build();
        return ResponseEntity.ok(response);
    }

    // lấy tất cả payment của cửa hàng
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GetPaymentResponse>>> getAllPaymentsByStore() {
        List<GetPaymentResponse> payments = paymentService.getAllPaymentsByStore();
        ApiResponse<List<GetPaymentResponse>> response = ApiResponse.<List<GetPaymentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("All payments retrieved successfully")
                .data(payments)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<String>>> getPaymentStatus() {
        List<String> statuses = Arrays.stream(PaymentStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Payment status retrieved successfully")
                .data(statuses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<String>>> getPaymentTypes() {
        List<String> types = Arrays.stream(PaymentType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Payment types retrieved successfully")
                .data(types)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/methods")
    public ResponseEntity<ApiResponse<List<String>>> getPaymentMethods() {
        List<String> methods = Arrays.stream(PaymentMethod.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Payment methods retrieved successfully")
                .data(methods)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/gateways")
    public ResponseEntity<ApiResponse<List<String>>> getPaymentGateways() {
        List<String> gateways = Arrays.stream(PaymentGateway.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .message("Payment gateways retrieved successfully")
                .data(gateways)
                .build();
        return ResponseEntity.ok(response);
    }

    // Confirm cash payment - no parameters, amount auto from payment
    @PutMapping("/{paymentId}/confirm-cash")
    public ResponseEntity<ApiResponse<GetPaymentResponse>> confirmCashPayment(@PathVariable int paymentId) {
        GetPaymentResponse payment = paymentService.confirmCashPayment(paymentId);
        ApiResponse<GetPaymentResponse> response = ApiResponse.<GetPaymentResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Xác nhận thanh toán tiền mặt thành công")
                .data(payment)
                .build();
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/vnpay/payment-url")
//    public ResponseEntity<ApiResponse<String>> createPaymentUrl(CreatePaymentUrlRequest request){
//        String paymentUrl = vnpayService.buildPaymentUrl(request);
//        ApiResponse<String> response = ApiResponse.<String>builder()
//                .code(HttpStatus.CREATED.value())
//                .message("VNPay payment URL generated successfully")
//                .data(paymentUrl)
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

//    @PostMapping("/vnpay/ipn")
//    public ResponseEntity<String> handleIPN(@RequestParam Map<String,String> params) {
//        if (!vnpayService.validateChecksum(params)) {
//            return ResponseEntity.badRequest().body("Invalid checksum");
//        }
//
//        String txnStatus = params.get("vnp_TransactionStatus");
//        String orderCode = params.get("vnp_TxnRef");
//        BigDecimal amount = new BigDecimal(params.get("vnp_Amount")).divide(BigDecimal.valueOf(100));
//
//        if ("00".equals(txnStatus)) {
//            paymentService.confirmPayment(orderCode, amount);
//            return ResponseEntity.ok("OK");
//        } else {
//            paymentService.markPaymentFailed(orderCode);
//            return ResponseEntity.ok("FAILED");
//        }
//    }

}
