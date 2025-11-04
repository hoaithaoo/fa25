package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.config.VNPayConfig;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.Transaction;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.PaymentStatus;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.TransactionStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.payment.PaymentRequest;
import swp391.fa25.saleElectricVehicle.repository.PaymentRepository;
import swp391.fa25.saleElectricVehicle.service.ContractService;
import swp391.fa25.saleElectricVehicle.service.PaymentService;
import swp391.fa25.saleElectricVehicle.service.TransactionService;
import swp391.fa25.saleElectricVehicle.service.VNPayService;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private VNPayService vNPayService;

    @Override
    public Payment getPaymentEntityById(int paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
    }

    @Override
    public Payment getPaymentEntityByPaymentCode(String paymentCode) {
        return paymentRepository.findPaymentByPaymentCode(paymentCode);
        // xử lí null ở vnpay service
    }

    public void confirmPayment(String paymentCode, BigDecimal amount) {
        // Tìm payment theo orderCode (tùy business, có thể là orderId hoặc mã khác)
        Payment payment = paymentRepository.findPaymentByPaymentCode(paymentCode);

        Transaction transaction = transactionService.createTransaction(payment.getPaymentId())

        Transaction txn = Transaction.builder()
                        .amount(amount)
                        .transactionTime(LocalDateTime.now())
                        .status(TransactionStatus.SUCCESS)
                        .gateway("VNPAY")
                        .payerInfor("IPN")
                        .note("Thanh toán qua VNPAY IPN thành công")
                        .payment(payment)
                        .build();

        txn.setPayment(payment);
        txn.setAmount(amount);
        txn.setTransactionTime(LocalDateTime.now());
        txn.setStatus("SUCCESS"); // mã hóa có thể lấy từ IPN params
        txn.setNote("Thanh toán qua VNPAY IPN thành công");
        transactionRepository.save(txn);

        boolean isEnough = vNPayService.validateAmount(paymentCode, amount);
        if (isEnough) {
            payment.setStatus(PaymentStatus.COMPLETED);
        }

        payment.setAmount(amount); // có thể cộng dồn nếu nhận từng phần
        payment.setConfirmedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Lưu transaction cho lần nhận IPN này (phiên bản tối giản)

    }
}
