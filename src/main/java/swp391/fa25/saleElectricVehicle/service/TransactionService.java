package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Transaction;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreateTransactionRequest;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetTransactionResponse;

public interface TransactionService {
    Transaction createTransaction(CreateTransactionRequest request);
//    GetTransactionResponse createTransaction(CreateTransactionRequest request);
}
