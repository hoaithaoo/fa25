/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp391.fa25.saleElectricVehicle.payload.request.payment.PaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.VNPayService;


/**
 *
 * @author CTT VNPAY
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createPayment(PaymentRequest request) {
        String paymentUrl = vnPayService.buildPaymentUrl(request);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.CREATED.value())
                .message("Payment URL created successfully")
                .data(paymentUrl)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @GetMapping("/create")
//    public ResponseEntity<?> createPayment() throws UnsupportedEncodingException {
//    }
}
