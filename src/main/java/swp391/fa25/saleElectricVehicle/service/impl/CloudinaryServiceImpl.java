package swp391.fa25.saleElectricVehicle.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.service.CloudinaryService;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    // nhận bean đã cấu hình trong CloudinaryConfig
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) { // tên thư mục muốn lưu
        try {
            // Xác định resource_type dựa trên content type của file
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            String resourceType = "auto"; // mặc định là auto cho ảnh
            
            // Nếu là PDF hoặc các file document khác, dùng "raw"
            if (contentType != null && (
                    contentType.equals("application/pdf") ||
                    contentType.equals("application/msword") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                    contentType.equals("application/vnd.ms-excel") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                    contentType.equals("application/vnd.ms-powerpoint") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")
            )) {
                resourceType = "raw";
            } else if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                // Fallback: kiểm tra extension nếu content type không có
                resourceType = "raw";
            }
            
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder, // chỉ định lưu vào folder nào trên cloudinary
                            "resource_type", resourceType // "raw" cho PDF/document, "auto" cho ảnh
                    )
            );

            // lấy link public (https://res.cloudinary.com/...) do cloudinary trả về
            // (đây là url có thể truy cập công khai), có thể gắn vào database / fe để show, truy cập file
            return uploadResult.get("secure_url").toString(); // URL public file
        } catch (IOException e) {
            throw new RuntimeException("Upload Cloudinary thất bại: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Upload Cloudinary thất bại - Lỗi không xác định: " + e.getMessage() + 
                    ". File type: " + file.getContentType() + ", File name: " + file.getOriginalFilename(), e);
        }
    }
}
