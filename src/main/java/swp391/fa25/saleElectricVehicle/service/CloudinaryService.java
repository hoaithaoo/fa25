package swp391.fa25.saleElectricVehicle.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folder);
}
