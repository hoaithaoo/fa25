package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.payload.request.payment.CreateTransactionRequest;
import swp391.fa25.saleElectricVehicle.payload.response.payment.GetTransactionResponse;

public interface TransactionService {
    GetTransactionResponse createTransaction(CreateTransactionRequest request);
}
