package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.config.VNPayConfig;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.Transaction;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.*;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreatePaymentRequest;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetPaymentResponse;
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
    private ContractService contractService;

    @Override
    public GetPaymentResponse createDraftPayment(CreatePaymentRequest request) {
        Contract contract = contractService.getContractEntityById(request.getContractId());

        // không cho thanh toán khi hợp đồng chưa ký,
        if (!contract.getStatus().equals(ContractStatus.SIGNED)) {
            throw new AppException(ErrorCode.CONTRACT_NOT_SIGNED);
        }

        if (request.getPaymentType() == PaymentType.DEPOSIT) {
            // Kiểm tra xem đã có payment đặt cọc chưa
            Optional<Payment> existingDepositPayment = paymentRepository
                    .findByContractAndPaymentType(contract, PaymentType.DEPOSIT);
            if (existingDepositPayment.isPresent()) {
                throw new AppException(ErrorCode.DEPOSIT_PAYMENT_ALREADY_EXISTS);
            }
        } else if (request.getPaymentType() == PaymentType.BALANCE) {
            // Kiểm tra xem đã có payment thanh toán số dư chưa
            Optional<Payment> existingBalancePayment = paymentRepository
                    .findByContractAndPaymentType(contract, PaymentType.BALANCE);
            if (existingBalancePayment.isPresent()) {
                throw new AppException(ErrorCode.BALANCE_PAYMENT_ALREADY_EXISTS);
            }
        }

        Payment payment = paymentRepository.save(Payment.builder()
                .status(PaymentStatus.DRAFT)
                .paymentType(request.getPaymentType())
                .paymentMethod(request.getPaymentMethod())
                .createdAt(LocalDateTime.now())
                .contract(contract)
                .build());

        if (PaymentType.DEPOSIT.equals(request.getPaymentType())) {
            payment.setPaymentCode("DP" + String.format("%06d", payment.getPaymentId()));
            payment.setAmount(contract.getDepositPrice()); // số tiền cần thanh toán
            payment.setRemainPrice(contract.getDepositPrice()); // số tiền còn lại cần thanh toán
        } else {
            payment.setPaymentCode("BL" + String.format("%06d", payment.getPaymentId()));
            payment.setAmount(contract.getRemainPrice()); // số tiền cần thanh toán
            payment.setRemainPrice(contract.getRemainPrice()); // số tiền còn lại cần thanh toán
        }

        // lưu lại khi đã có payment code
        paymentRepository.save(payment);

        return GetPaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentCode(payment.getPaymentCode())
                .remainPrice(payment.getRemainPrice())
                .status(payment.getStatus())
                .paymentType(payment.getPaymentType())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .contractCode(contract.getContractCode())
                .build();
    }

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

    @Override
    public void confirmPayment(Payment payment, BigDecimal amount) {
        // Tìm payment theo orderCode (tùy business, có thể là orderId hoặc mã khác)
//        Payment payment = getPaymentEntityById(paymentId);

//        Transaction transaction = transactionService.createTransaction(payment.getPaymentId())
//
//        Transaction txn = Transaction.builder()
//                        .amount(amount)
//                        .transactionTime(LocalDateTime.now())
//                        .status(TransactionStatus.SUCCESS)
//                        .gateway("VNPAY")
//                        .payerInfor("IPN")
//                        .note("Thanh toán qua VNPAY IPN thành công")
//                        .payment(payment)
//                        .build();
//
//        txn.setPayment(payment);
//        txn.setAmount(amount);
//        txn.setTransactionTime(LocalDateTime.now());
//        txn.setStatus("SUCCESS"); // mã hóa có thể lấy từ IPN params
//        txn.setNote("Thanh toán qua VNPAY IPN thành công");
//        transactionRepository.save(txn);

//        boolean isEnough = vnpayService.validateAmount(paymentCode, amount);
//        if (isEnough) {
//            payment.setStatus(PaymentStatus.COMPLETED);
//        }
        payment.setRemainPrice(payment.getAmount().subtract(amount)); // tiền còn lại = tiền phải trả - tiền đã trả ở lần này
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Lưu transaction cho lần nhận IPN này (phiên bản tối giản)

    }
}
