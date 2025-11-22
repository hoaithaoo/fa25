package swp391.fa25.saleElectricVehicle.service;

public interface TokenBlacklistService {
    void addToBlacklist(String token, long ttlMillis);
    boolean isBlacklisted(String token);
    void cleanupExpiredTokens();
}
