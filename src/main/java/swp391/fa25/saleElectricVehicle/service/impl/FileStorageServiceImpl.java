package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.service.FileStorageService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

//    // class trong java nio đại diện cho đường dẫn trong hệ thống file
//    private final Path rootLocation;
//
//    // lấy từ application.properties convert sang Path bằng constructor
//    public FileStorageServiceImpl(@Value("${file.upload.dir}") String uploadDir) {
//        this.rootLocation = Paths.get(uploadDir) // tạo đối tượng Path từ chuỗi uploadDir
//                .toAbsolutePath()
//                .normalize();
//    }
//
//    @Override
//    public String uploadFile(MultipartFile file, String folder) {
//        try {
//            // Đảm bảo thư mục tồn tại
//            Path destDir = this.rootLocation.resolve(folder);
//            if (!Files.exists(destDir)) Files.createDirectories(destDir);
//
//            // Lưu file
//            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            Path filePath = destDir.resolve(filename);
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            // Trả về URL hoặc path file đã lưu
//            return "${file.upload.dir}" + folder + "/" + filename;
//        } catch (Exception e) {
//            throw new RuntimeException("Không thể lưu file: " + e.getMessage());
//        }
//    }
}
