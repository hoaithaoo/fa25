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
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder, // chỉ định lưu vào folder nào trên cloudinary
                            "resource_type", "auto" // tự nhận dạng file pdf, ảnh v.v.
                    )
            );

            // lấy link public (https://res.cloudinary.com/...) do cloudinary trả về
            // (đây là url có thể truy cập công khai), có thể gắn vào database / fe để show, truy cập file
            return uploadResult.get("secure_url").toString(); // URL public file
        } catch (IOException e) {
            throw new RuntimeException("Upload Cloudinary thất bại: " + e.getMessage());
        }
    }
}
