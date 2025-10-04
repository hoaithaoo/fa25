package swp391.fa25.saleElectricVehicle.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //define error code
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN(1001, "Token không đúng", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "Tài khoản đã tồn tại", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    STORE_NOT_EXIST(1004, "Không tìm thấy store", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXIST(1005, "Không tìm thấy role", HttpStatus.NOT_FOUND),
    USER_NOT_EXIST(1006, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(1007, "Sai mật khẩu", HttpStatus.UNAUTHORIZED),
    MODEL_NOT_FOUND(1008, "Model không tồn tại", HttpStatus.NOT_FOUND),
    MODEL_EXISTED(1009, "Model đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1010, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_EXIST(1011, "Khuyến mãi không tồn tại", HttpStatus.NOT_FOUND),
    PROMOTION_EXISTED(1012, "Khuyến mãi đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT(1013, "Số tiền không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_END_DATE(1014, "Ngày kết thúc không được trước ngày bắt đầu", HttpStatus.BAD_REQUEST),
    COLOR_NOT_EXIST(1015, "Màu sắc không tồn tại", HttpStatus.NOT_FOUND),
    COLOR_EXISTED(1016, "Màu sắc đã tồn tại", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(1017, "Số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(1018, "Role đã tồn tại", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}