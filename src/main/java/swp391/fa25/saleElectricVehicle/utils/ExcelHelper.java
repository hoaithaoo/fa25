package swp391.fa25.saleElectricVehicle.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import swp391.fa25.saleElectricVehicle.entity.Vehicle;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.VehicleStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // Check xem file up lên có đúng định dạng Excel không
    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Vehicle> excelToVehicles(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0); // Lấy Sheet đầu tiên
            List<Vehicle> vehicles = new ArrayList<>();

            // DataFormatter giúp chuyển mọi cell (kể cả số) thành String chuẩn
            DataFormatter dataFormatter = new DataFormatter();

            int rowIndex = 0;
            for (Row row : sheet) {
                // Bỏ qua dòng Header (dòng 0)
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }

                // Bỏ qua dòng trống
                if (row == null || isRowEmpty(row)) continue;

                Vehicle vehicle = new Vehicle();

                // Cột 0: Mã VIN
                String vin = dataFormatter.formatCellValue(row.getCell(0));
                // Cột 1: Số máy
                String engineNo = dataFormatter.formatCellValue(row.getCell(1));
                // Cột 2: Số Pin
                String batteryNo = dataFormatter.formatCellValue(row.getCell(2));

                vehicle.setVin(vin.trim().toUpperCase());
                vehicle.setEngineNo(engineNo.trim());
                vehicle.setBatteryNo(batteryNo.trim());
                
                // Set trạng thái mặc định là "Đang vận chuyển"
//                vehicle.setStatus(VehicleStatus.IN_TRANSIT);

                vehicles.add(vehicle);
            }

            workbook.close();
            return vehicles;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi phân tích file Excel: " + e.getMessage());
        }
    }

    // Hàm phụ check dòng trống
    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }
}