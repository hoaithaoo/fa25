package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.config.VNPayConfig;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.entity.Order;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.ContractStatus;
import swp391.fa25.saleElectricVehicle.entity.Payment;
import swp391.fa25.saleElectricVehicle.entity.Transaction;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.*;
import swp391.fa25.saleElectricVehicle.payload.request.payment.CreateTransactionRequest;
import swp391.fa25.saleElectricVehicle.service.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ContractService contractService;

    @Override
    public String buildPaymentUrl(int paymentId, HttpServletRequest request) {
        Payment payment = paymentService.getPaymentEntityById(paymentId);

//        Contract contract = contractService.getContractEntityById(payment.getContract().getContractId());
//        if (payment.getPaymentType().equals(PaymentType.DEPOSIT))
        BigDecimal amount = payment.getAmount().multiply(BigDecimal.valueOf(100));
//        BigDecimal amount = contract.getTotalPayment().multiply(BigDecimal.valueOf(100));

        try {
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "pay";
//        String orderType = "other";
//        long amount = Integer.parseInt(req.getParameter("amount")) * 100;
//        String bankCode = req.getParameter("bankCode");
            // lấy payment code làm mã tham chiếu giao dịch của merchant,
            // dùng để phân biệt các đơn hàng gửi sang VNPAY.
            // phân biệt cả loại thanh toán (cọc hay còn lại)
            String vnp_TxnRef = payment.getPaymentCode();
//            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
            String vnp_IpAddr = VNPayConfig.getIpAddress(request);

            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

            // đưa vào map các tham số
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", amount.toBigInteger().toString());
            vnp_Params.put("vnp_CurrCode", "VND");
//            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_OrderType", VNPayConfig.vnp_OrderType);
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
//        if (bankCode != null && !bankCode.isEmpty()) {
//            vnp_Params.put("vnp_BankCode", bankCode);
//        }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef + ". So tien " + amount + " VND");
//        vnp_Params.put("vnp_OrderType", orderType);
//        String locate = req.getParameter("language");
//        if (locate != null && !locate.isEmpty()) {
//            vnp_Params.put("vnp_Locale", locate);
//        } else {
//            vnp_Params.put("vnp_Locale", "vn");
//        }
//        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

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

    private boolean validateChecksum(Map<String, String> params) throws UnsupportedEncodingException {
        // lấy chữ ký từ params rồi bỏ các trường chữ ký trước khi dựng chuỗi để tính lại hash
        // lấy giá trị chữ ký VNPay gửi kèm (chuỗi hexa hash)
        // Cần giữ lại để so sánh với hash tự tính
        String vnp_SecureHash = params.get("vnp_SecureHash");
        // Xóa trường vnp_SecureHashType khỏi params vì trường này không tham gia tính hash
        // (theo spec VNPay, cần loại bỏ các trường hash trước khi compute)
        params.remove("vnp_SecureHashType");
        // Xóa vnp_SecureHash khỏi params — tránh vô tình dùng chính nó khi dựng chuỗi để hash
        params.remove("vnp_SecureHash");

        // chuẩn bị chuỗi dữ liệu theo thứ tự khóa (alphabetical) để compute HMAC
        // Lấy danh sách tất cả key còn lại trong params
        List<String> fieldNames = new ArrayList<>(params.keySet());
        //Sắp xếp tên trường theo thứ tự lexicographical (theo yêu cầu VNPay: phải sort key khi tạo chuỗi check)
        Collections.sort(fieldNames);
        // Tạo StringBuilder để ghép các cặp key=value thành chuỗi dùng để hash
        StringBuilder hashData = new StringBuilder();
        // Cờ để xử lý dấu & giữa các cặp; tránh thêm & trước cặp đầu
        boolean first = true;
        for (String fieldName : fieldNames) {
            // Lấy giá trị tương ứng của field
            String fieldValue = params.get(fieldName);
            // Bỏ qua các field rỗng/null — theo spec thường chỉ đưa vào các field có dữ liệu
            if (fieldValue != null && fieldValue.length() > 0) {
                // Nếu không phải cặp đầu thì thêm dấu & như key1=val1&key2=val2...
                if (!first) {
                    hashData.append('&');
                }
                // Thêm key=value vào chuỗi. Quan trọng: URLEncoder.encode(...) để encode value theo percent-encoding (UTF-8).
                // Điều này đảm bảo chuỗi hash khớp với cách VNPay tạo chuỗi (nếu VNPay yêu cầu encode các giá trị).
                //Lưu ý: phải dùng cùng cách encode (và cùng charset) với spec VNPay;
                // nếu VNPay không yêu cầu encode tên key nhưng yêu cầu encode value thì làm như trên. Nếu spec khác, cần chỉnh
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.name()));
                first = false;
            }
        }

        // Gọi hàm helper hmacSHA512 (thuộc VNPayConfig) để tính HMAC-SHA512 của chuỗi hashData với secretKey
        // (chìa khoá bí mật do VNPay cấp).
        // Kết quả là một chuỗi hex (hoặc base16) — phải cùng định dạng (ví dụ hex lowercase/uppercase)
        // so với vnp_SecureHash để so sánh
        String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        return calculatedHash.equalsIgnoreCase(vnp_SecureHash);
    }

    private boolean validateAmount(Payment payment, BigDecimal amount) {
        boolean result = false;
        if (amount.compareTo(payment.getAmount()) == 0) {
            result = true;
        }
        return result;
    }

//    // khai báo hàm công khai trả về Map<String, String> - nơi chứa mã phản hồi (rspCode) và thông báo (message)
//    // tham số đầu vào là Map<String, String> params - chứa tất cả các tham số VNPAY gửi về trong IPN (từ query string or body)
//    public Map<String, String> processIpn(Map<String, String> params) {
    @Override
    public Map<String, String> processIpn(Map<String, String> params) {
        // tạo map rỗng để chứa kết quả trả về cho VNPAY
        // ví dụ {"RspCode":"00","Message":"Confirm Success"}
        Map<String, String> response = new HashMap<>();
        try {
            // validate checksum
            boolean isValidChecksum = validateChecksum(params);
            if (!isValidChecksum) {
//                throw new AppException(ErrorCode.INVALID_CHECKSUM);
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
//                return response;
            }

            // lấy các tham số cần thiết từ params
            String paymentCode = params.get("vnp_TxnRef");
            // TODO: Validate vnp_TxnRef exists in DB
//            String txnRef = params.get("vnp_TxnRef");
            // vì đã đặt mã đơn hàng là mã của payment
            // để phân biệt thanh toán là của deposit hay balance
            Payment payment = paymentService.getPaymentEntityByPaymentCode(paymentCode);
//            boolean orderExists = true; // replace with real check
//            if (!orderExists) {
            if (payment == null) {
                response.put("RspCode", "01");
                response.put("Message", "Payment not Found");
//                response.put("Message", "Order not Found");
//                return response;
            }

            // validate đã thanh toán payment này chưa để tránh xử lí trùng
            if (payment.getStatus().equals(PaymentStatus.COMPLETED)) {
                response.put("RspCode", "02");
                response.put("Message", "Payment already confirmed");
//                return response;
            }

            String vnp_TransactionNo = params.get("vnp_TransactionNo");
            BigDecimal amount = new BigDecimal(params.get("vnp_Amount")).divide(BigDecimal.valueOf(100));
            String vnp_BankTranNo = params.get("vnp_BankTranNo");

            String payDate = params.get("vnp_PayDate");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime vnp_PayDate = LocalDateTime.parse(payDate, formatter);

            String responseCode = params.get("vnp_ResponseCode");
            TransactionStatus status = getTransactionStatus(responseCode);

            CreateTransactionRequest transactionRequest = CreateTransactionRequest.builder()
                    .paymentCode(paymentCode)
                    .transactionRef(vnp_TransactionNo)
                    .amount(amount)
                    .transactionDate(vnp_PayDate)
                    .bankTransactionCode(vnp_BankTranNo)
                    .gateway(PaymentGateway.VNPAY)
                    .status(status)
                    .build();

            // tạo transaction ghi lại thông tin của lần thanh toán này
            Transaction transaction = transactionService.createTransaction(transactionRequest);

            boolean validAmount = validateAmount(payment, amount);
            if (!validAmount) {
                response.put("RspCode", "04");
                response.put("Message", "Invalid Amount");
//                return response;
            }

//            // lấy chữ ký từ params rồi bỏ các trường chữ ký trước khi dựng chuỗi để tính lại hash
//            // lấy giá trị chữ ký VNPay gửi kèm (chuỗi hexa hash)
//            // Cần giữ lại để so sánh với hash tự tính
//            String vnp_SecureHash = params.get("vnp_SecureHash");
//            // Xóa trường vnp_SecureHashType khỏi params vì trường này không tham gia tính hash
//            // (theo spec VNPay, cần loại bỏ các trường hash trước khi compute)
//            params.remove("vnp_SecureHashType");
//            // Xóa vnp_SecureHash khỏi params — tránh vô tình dùng chính nó khi dựng chuỗi để hash
//            params.remove("vnp_SecureHash");
//
//            // chuẩn bị chuỗi dữ liệu theo thứ tự khóa (alphabetical) để compute HMAC
//            // Lấy danh sách tất cả key còn lại trong params
//            List<String> fieldNames = new ArrayList<>(params.keySet());
//            //Sắp xếp tên trường theo thứ tự lexicographical (theo yêu cầu VNPay: phải sort key khi tạo chuỗi check)
//            Collections.sort(fieldNames);
//            // Tạo StringBuilder để ghép các cặp key=value thành chuỗi dùng để hash
//            StringBuilder hashData = new StringBuilder();
//            // Cờ để xử lý dấu & giữa các cặp; tránh thêm & trước cặp đầu
//            boolean first = true;
//            for (String fieldName : fieldNames) {
//                // Lấy giá trị tương ứng của field
//                String fieldValue = params.get(fieldName);
//                // Bỏ qua các field rỗng/null — theo spec thường chỉ đưa vào các field có dữ liệu
//                if (fieldValue != null && fieldValue.length() > 0) {
//                    // Nếu không phải cặp đầu thì thêm dấu & như key1=val1&key2=val2...
//                    if (!first) {
//                        hashData.append('&');
//                    }
//                    // Thêm key=value vào chuỗi. Quan trọng: URLEncoder.encode(...) để encode value theo percent-encoding (UTF-8).
//                    // Điều này đảm bảo chuỗi hash khớp với cách VNPay tạo chuỗi (nếu VNPay yêu cầu encode các giá trị).
//                    //Lưu ý: phải dùng cùng cách encode (và cùng charset) với spec VNPay;
//                    // nếu VNPay không yêu cầu encode tên key nhưng yêu cầu encode value thì làm như trên. Nếu spec khác, cần chỉnh
//                    hashData.append(fieldName).append('=')
//                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.name()));
//                    first = false;
//                }
//            }
//
//            // Gọi hàm helper hmacSHA512 (thuộc VNPayConfig) để tính HMAC-SHA512 của chuỗi hashData với secretKey
//            // (chìa khoá bí mật do VNPay cấp).
//            // Kết quả là một chuỗi hex (hoặc base16) — phải cùng định dạng (ví dụ hex lowercase/uppercase)
//            // so với vnp_SecureHash để so sánh
//            String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
//            // So sánh chữ ký tính được và chữ ký VNPay gửi. equalsIgnoreCase dùng để tránh khác biệt hoa/thường.
//            // Nếu không khớp => dữ liệu đã bị thay đổ
//            if (!calculatedHash.equalsIgnoreCase(vnp_SecureHash)) {
//                // Đặt mã lỗi 97 — theo luật của code này: 97 = Invalid Checksum (chữ ký không hợp lệ)
//                response.put("RspCode", "97");
//                response.put("Message", "Invalid Checksum");
//                return response;
//            }
//
//            // TODO: Validate vnp_TxnRef exists in DB
//            String txnRef = params.get("vnp_TxnRef");
//            // vì đã đặt mã đơn hàng là mã của payment
//            // để phân biệt thanh toán là của deposit hay balance
//            Payment payment = paymentService.getPaymentEntityByPaymentCode(txnRef);
////            boolean orderExists = true; // replace with real check
////            if (!orderExists) {
//            if (payment == null) {
//                response.put("RspCode", "01");
//                response.put("Message", "Payment not Found");
////                response.put("Message", "Order not Found");
//                return response;
//            }
//
//            // tạo transaction ghi lại thông tin của lần thanh toán này
//
//
//            // TODO: Validate amount
//            Contract contract = payment.getContract();
////            boolean validAmount = true; // replace with real check
////            if (!validAmount) {
////                response.put("RspCode", "04");
////                response.put("Message", "Invalid Amount");
////                return response;
////            }
//
//            // TODO: Check and update order status to prevent double processing
//            boolean notAlreadyConfirmed = true; // replace with real check
//            if (!notAlreadyConfirmed) {
//                response.put("RspCode", "02");
//                response.put("Message", "Order already confirmed");
//                return response;
//            }
//
//            String responseCode = params.get("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                // Cập nhật payment status thành COMPLETED
                paymentService.updatePaymentStatus(payment, amount, PaymentStatus.COMPLETED);

                // Cập nhật order paidAmount
                Order order = payment.getOrder();
                order.setPaidAmount(order.getPaidAmount().add(amount));
                orderService.updateOrder(order);

                // Nếu là payment deposit và đã có contract đặt cọc, update contract status thành DEPOSIT_PAID
                if (payment.getPaymentType() == PaymentType.DEPOSIT) {
                    if (contractService.hasDepositContract(order.getOrderId())) {
                        Contract depositContract = contractService.getDepositContractByOrderId(order.getOrderId());
                        contractService.updateContractStatus(depositContract, ContractStatus.DEPOSIT_PAID);
                    }
                } else if (payment.getPaymentType() == PaymentType.BALANCE) {
                    // Nếu là payment balance, tìm contract mua bán và update status thành FULLY_PAID
                    if (contractService.hasSaleContract(order.getOrderId())) {
                        Contract saleContract = contractService.getSaleContractByOrderId(order.getOrderId());
                        contractService.updateContractStatus(saleContract, ContractStatus.FULLY_PAID);
                    }
                    
                    // Kiểm tra xem đã thanh toán đủ chưa (paidAmount >= totalPayment)
                    if (order.getPaidAmount().compareTo(order.getTotalPayment()) >= 0) {
                        // Update order status thành FULLY_PAID
                        orderService.updateOrderStatus(order, OrderStatus.FULLY_PAID);
                    }
                }

                // TODO: mark order as paid in DB
                response.put("RspCode", "00");
                response.put("Message", "Confirm Success");
            } else {
                paymentService.updatePaymentStatus(payment, amount, PaymentStatus.CANCELLED);
//                // TODO: mark order as failed in DB
//                response.put("RspCode", "00");
//                response.put("Message", "Confirm Success"); // still return 00 to acknowledge IPN receipt
            }

//            return response;
        } catch (Exception ex) {
//            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
//            return response;
        }
        return response;
    }

    private static TransactionStatus getTransactionStatus(String responseCode) {
        TransactionStatus status;
        if ("00".equals(responseCode)) {
            status = TransactionStatus.SUCCESS;
        } else {
            status = TransactionStatus.FAILED;
        }
        return status;
    }
}
