package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.config.VNPayConfig;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.payload.request.payment.PaymentRequest;
import swp391.fa25.saleElectricVehicle.service.ContractService;
import swp391.fa25.saleElectricVehicle.service.VNPayService;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {

    @Autowired
    private ContractService contractService;

    @Override
    public String buildPaymentUrl(PaymentRequest paymentRequest) {
        Contract contract = contractService.getContractEntityById(paymentRequest.getContractId());
        BigDecimal amount = contract.getTotalPayment().multiply(BigDecimal.valueOf(100));

        try {
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "pay";
//        String orderType = "other";
//        long amount = Integer.parseInt(req.getParameter("amount")) * 100;
//        String bankCode = req.getParameter("bankCode");

            // fix cứng để test

            // lấy contract code làm mã tham chiếu giao dịch của merchant,
            // dùng để phân biệt các đơn hàng gửi sang VNPAY.
            String vnp_TxnRef = contract.getContractCode();
//            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
//        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

            // đưa vào map các tham số
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "NCB"); // test default là NCB (trong mail phần ngân hàng)
            vnp_Params.put("vnp_Locale", "vn");

//        if (bankCode != null && !bankCode.isEmpty()) {
//            vnp_Params.put("vnp_BankCode", bankCode);
//        }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef); // mã đơn hàng (?)
//        vnp_Params.put("vnp_OrderType", orderType);

//        String locate = req.getParameter("language");
//        if (locate != null && !locate.isEmpty()) {
//            vnp_Params.put("vnp_Locale", locate);
//        } else {
//            vnp_Params.put("vnp_Locale", "vn");
//        }
//        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
//        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // mã hóa các tham số
            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            // dán chuỗi mã hóa vào url
            String queryUrl = query.toString();
            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//            String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
            return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
//        com.google.gson.JsonObject job = new JsonObject();
//        job.addProperty("code", "00");
//        job.addProperty("message", "success");
//        job.addProperty("data", paymentUrl);
//        Gson gson = new Gson();
//        resp.getWriter().write(gson.toJson(job));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
