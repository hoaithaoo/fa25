package swp391.fa25.saleElectricVehicle.service;

public interface DepositAgreementService {
    String generateDepositAgreementHtml(int orderId);
    void createDepositRequest(int orderId);
}

