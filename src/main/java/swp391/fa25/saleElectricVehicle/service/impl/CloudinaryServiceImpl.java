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

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto" // tự nhận dạng file pdf, ảnh v.v.
                    )
            );
            return uploadResult.get("secure_url").toString(); // URL public file
        } catch (IOException e) {
            throw new RuntimeException("Upload Cloudinary thất bại: " + e.getMessage());
        }
    }
}
