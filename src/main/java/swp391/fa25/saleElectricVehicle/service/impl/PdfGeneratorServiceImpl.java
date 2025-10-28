package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.service.FileStorageService;
import swp391.fa25.saleElectricVehicle.service.PdfGeneratorService;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Generate PDF hợp đồng từ template
     * Dùng thư viện: iText, Apache PDFBox, hoặc JasperReports
     */
//    public String generateContractPdf(Contract contract) {
//        try {
//            // 1. Load template PDF hoặc tạo mới
//            Document document = new Document();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            PdfWriter.getInstance(document, baos);
//
//            document.open();
//
//            // 2. Add content
//            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
//            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
//
//            // Title
//            Paragraph title = new Paragraph("HỢP ĐỒNG MUA BÁN XE", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//
//            document.add(new Paragraph("\n"));
//
//            // Contract number
//            document.add(new Paragraph("Số hợp đồng: " + contract.getContractNumber(), normalFont));
//            document.add(new Paragraph("Ngày ký: " + contract.getContractDate(), normalFont));
//            document.add(new Paragraph("\n"));
//
//            // Bên A (Công ty)
//            document.add(new Paragraph("BÊN A: CÔNG TY BÁN XE ĐIỆN ABC", normalFont));
//            document.add(new Paragraph("Địa chỉ: 123 Nguyễn Văn Linh, Q7, TP.HCM", normalFont));
//            document.add(new Paragraph("\n"));
//
//            // Bên B (Khách hàng)
//            Customer customer = contract.getOrder().getCustomer();
//            document.add(new Paragraph("BÊN B: " + customer.getName().toUpperCase(), normalFont));
//            document.add(new Paragraph("CMND/CCCD: " + customer.getIdCard(), normalFont));
//            document.add(new Paragraph("Địa chỉ: " + customer.getAddress(), normalFont));
//            document.add(new Paragraph("SĐT: " + customer.getPhone(), normalFont));
//            document.add(new Paragraph("\n"));
//
//            // Nội dung hợp đồng
//            document.add(new Paragraph("ĐIỀU 1: ĐỐI TƯỢNG HỢP ĐỒNG", titleFont));
//            document.add(new Paragraph(contract.getTerms(), normalFont));
//            document.add(new Paragraph("\n"));
//
//            // Danh sách xe
//            document.add(new Paragraph("ĐIỀU 2: DANH SÁCH XE MUA BÁN", titleFont));
//
//            PdfPTable table = new PdfPTable(5);
//            table.addCell("STT");
//            table.addCell("Model");
//            table.addCell("Màu");
//            table.addCell("Số lượng");
//            table.addCell("Đơn giá");
//
//            int stt = 1;
//            for (OrderDetail detail : contract.getOrder().getOrderDetails()) {
//                table.addCell(String.valueOf(stt++));
//                table.addCell(detail.getStoreStock().getModelColor().getModel().getModelName());
//                table.addCell(detail.getStoreStock().getModelColor().getColor().getColorName());
//                table.addCell(String.valueOf(detail.getQuantity()));
//                table.addCell(detail.getUnitPrice().toString());
//            }
//            document.add(table);
//            document.add(new Paragraph("\n"));
//
//            // Tổng tiền
//            document.add(new Paragraph("Tổng giá trị hợp đồng: " +
//                    contract.getTotalAmount().toString() + " VNĐ",
//                    normalFont));
//            document.add(new Paragraph("\n"));
//
//            // Phương thức thanh toán
//            document.add(new Paragraph("ĐIỀU 3: PHƯƠNG THỨC THANH TOÁN", titleFont));
//            document.add(new Paragraph("Phương thức: " + contract.getPaymentMethod(), normalFont));
//            document.add(new Paragraph("\n\n\n"));
//
//            // Chữ ký
//            PdfPTable signatureTable = new PdfPTable(2);
//            signatureTable.addCell("BÊN A\n\n\n\n(Ký và ghi rõ họ tên)");
//            signatureTable.addCell("BÊN B\n\n\n\n(Ký và ghi rõ họ tên)");
//            document.add(signatureTable);
//
//            document.close();
//
//            // 3. Save to storage
//            byte[] pdfBytes = baos.toByteArray();
//            String fileName = "contract_" + contract.getContractNumber() + "_unsigned.pdf";
//            String fileUrl = fileStorageService.saveFile(pdfBytes, fileName, "contracts/unsigned");
//
//            return fileUrl;
//
//        } catch (Exception e) {
//            throw new AppException(ErrorCode.PDF_GENERATION_FAILED);
//        }
//    }
}
