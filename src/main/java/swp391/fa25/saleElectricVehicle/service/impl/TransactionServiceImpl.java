package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.Transaction;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreateTransactionRequest;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetTransactionResponse;
import swp391.fa25.saleElectricVehicle.repository.TransactionRepository;
import swp391.fa25.saleElectricVehicle.service.PaymentService;
import swp391.fa25.saleElectricVehicle.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentService paymentService;

    @Override
    public Transaction createTransaction(CreateTransactionRequest request) {
        Payment payment = paymentService.getPaymentEntityByPaymentCode(request.getPaymentCode());
//        Payment payment = paymentService.getPaymentEntityById(request.getPaymentId());
        Transaction transaction = Transaction.builder()
                .payment(payment)
                .transactionRef(request.getTransactionRef())
                .amount(request.getAmount())
                .transactionTime(request.getTransactionDate())
                .gateway(request.getGateway())
                .bankTransactionCode(request.getBankTransactionCode())
//                .payerInfor(request.getPayerInfor())
//                .note(request.getNote())
                .status(request.getStatus())
                .build();
        return transactionRepository.save(transaction);
//        return GetTransactionResponse.builder()
//                .transactionId(savedTransaction.getTransactionId())
//                .transactionRef(savedTransaction.getTransactionRef())
//                .amount(savedTransaction.getAmount())
//                .transactionDate(savedTransaction.getTransactionTime())
//                .gateway(payment.getGateway())
//                .payerInfor(savedTransaction.getPayerInfor())
//                .note(savedTransaction.getNote())
//                .status(savedTransaction.getStatus())
//                .build();
    }
}
