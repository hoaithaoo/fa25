package swp391.fa25.saleElectricVehicle.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentUrlRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetPaymentResponse;
import swp391.fa25.saleElectricVehicle.service.PaymentService;
import swp391.fa25.saleElectricVehicle.service.VNPayService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
