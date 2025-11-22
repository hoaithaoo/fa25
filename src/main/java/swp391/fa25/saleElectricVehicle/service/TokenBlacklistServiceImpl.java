package swp391.fa25.saleElectricVehicle.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    
    // Lưu token và thời gian hết hạn (timestamp)
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();
    
    // Scheduled executor để cleanup token hết hạn
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * Thêm token vào blacklist với TTL (Time To Live)
     * @param token Token cần blacklist
     * @param ttlMillis Thời gian sống (milliseconds) - thường là thời gian hết hạn của token
     */
    @Override
    public void addToBlacklist(String token, long ttlMillis) {
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        blacklist.put(token, expiryTime);
        
        // Tự động xóa sau TTL
        scheduler.schedule(() -> {
            blacklist.remove(token);
        }, ttlMillis, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Kiểm tra token có trong blacklist không
     * @param token Token cần kiểm tra
     * @return true nếu token bị blacklist, false nếu không
     */
    @Override
    public boolean isBlacklisted(String token) {
        Long expiryTime = blacklist.get(token);
        
        if (expiryTime == null) {
            return false; // Không có trong blacklist
        }
        
        // Kiểm tra đã hết hạn chưa
        if (System.currentTimeMillis() > expiryTime) {
            blacklist.remove(token); // Xóa nếu hết hạn
            return false;
        }
        
        return true; // Có trong blacklist và chưa hết hạn
    }
    
    /**
     * Xóa token khỏi blacklist (nếu cần)
     */
//    public void removeFromBlacklist(String token) {
//        blacklist.remove(token);
//    }
    
    /**
     * Cleanup token hết hạn định kỳ (mỗi 30 phút)
     */
    @Override
    @Scheduled(fixedRate = 1800000) // 30 phút
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
    
    /**
     * Lấy số lượng token trong blacklist (để monitoring)
     */
//    public int getBlacklistSize() {
//        return blacklist.size();
//    }
}

