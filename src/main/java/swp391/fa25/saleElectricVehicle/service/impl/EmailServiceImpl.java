package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendNewMail(String to, String subject, String body, String fullname) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // Khởi tạo MimeMessageHelper với tham số 'true' cho phép nội dung HTML
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        // Chỉ set text HTML đã được định dạng sẵn từ hàm gọi
        helper.setText(body, true);

        mailSender.send(message);
    }

    @Override
    public void sendVerificationLink(String email, String verificationUrl, String customerFullname) throws MessagingException {

        String subject = "Account Verification Required - TixClick";

        // Thay đổi nội dung email để chứa liên kết
        String body = "<html>" +
                "<body>" +
                "<h2 style=\"color: #0D6EFD;\">Kích hoạt tài khoản</h2>" +
                "<p>Xin chào " +  customerFullname +",</p>" +
                "<p>Cảm ơn bạn đã đăng ký mua xe tại Electra. Vui lòng nhấp vào liên kết bên dưới để kích hoạt tài khoản và hoàn tất quá trình đăng ký.</p>" +

                // Nút bấm kích hoạt
//                "<p>" +
//                "<a href=\"" + verificationUrl + "\" style=\"display: inline-block; padding: 10px 20px; color: #ffffff; background-color: #0D6EFD; text-decoration: none; border-radius: 5px; font-weight: bold;\">" +
//                "Kích hoạt tài khoản" +
//                "</a>" +
//                "</p>" +

                // Fallback
//                "<p style=\"font-size: small; color: #6c757d;\">Nếu nút trên không hoạt động, vui lòng sao chép và dán liên kết sau vào trình duyệt của bạn:</p>" +
                "<p style=\"font-size: small;\"><a href=\"" + verificationUrl + "\">" + verificationUrl + "</a></p>" +

                "<p>Liên kết kích hoạt này sẽ hết hạn trong 60 phút.</p>" +
                "<p>Cảm ơn bạn đã sử dụng dịch vụ của Electra!</p>" +
                "<p>Trân trọng,<br/>Đội ngũ Electra</p>" +
                "</body>" +
                "</html>";

        // Giữ nguyên hàm gửi mail cơ bản của bạn
        sendNewMail(email, subject, body, customerFullname);
    }

}
