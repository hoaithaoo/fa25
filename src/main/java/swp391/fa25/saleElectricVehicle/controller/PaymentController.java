package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentUrlRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetPaymentResponse;
import swp391.fa25.saleElectricVehicle.service.PaymentService;
import swp391.fa25.saleElectricVehicle.service.VNPayService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VNPayService vnpayService;

    @PostMapping("/create")
    public String createPayment(@RequestParam CreatePaymentRequest request) {
        GetPaymentResponse payment = paymentService.createDraftPayment(request);
        return "/payment/vnpay/payment-url";
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
