package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentUrlRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.VNPayService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnpayService;

//    @GetMapping("/payment-url")
//    public ResponseEntity<ApiResponse<String>> createPaymentUrl(CreatePaymentUrlRequest request){
//        String paymentUrl = vnpayService.buildPaymentUrl(request);
//        ApiResponse<String> response = ApiResponse.<String>builder()
//                .code(HttpStatus.CREATED.value())
//                .message("VNPay payment URL generated successfully")
//                .data(paymentUrl)
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> handleIPN(@RequestParam Map<String,String> params) {
        Map<String, String> response = vnpayService.processIpn(params);
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/ipn")
//    public ResponseEntity<ApiResponse<String>> handleIPN(@RequestParam Map<String, String> params) {
//        vnpayService.processIpn(params);
//        ApiResponse<String> response = ApiResponse.<String>builder()
//                .code(HttpStatus.OK.value())
//                .message("VNPay IPN processed successfully")
//                .data(string)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
}
