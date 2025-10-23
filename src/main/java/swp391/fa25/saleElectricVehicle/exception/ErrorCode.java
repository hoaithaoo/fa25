
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
    STORE_EXISTED(1019, "Store đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_END_DATE_TIME(1020, "Ngày kết thúc không được trước ngày hiện tại", HttpStatus.BAD_REQUEST),
    MODEL_COLOR_NOT_EXIST(1021, "Model màu không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_EXISTED(1021, "Đơn hàng đã tồn tại", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXIST(1022, "Đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_DETAIL_NOT_FOUND(1023, "Chi tiết đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    STORE_STOCK_NOT_FOUND(1024, "Kho hàng không tồn tại", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(1025, "Kho hàng không đủ", HttpStatus.BAD_REQUEST),
    ROLE_CANNOT_ASSIGN_STORE(1026, "Admin và EVM Staff không được gán store", HttpStatus.BAD_REQUEST),
    STORE_STOCK_EXISTED(1027, "Kho hàng đã tồn tại", HttpStatus.BAD_REQUEST),
    COLOR_CODE_EXISTED(1028, "Mã màu đã tồn tại", HttpStatus.FORBIDDEN),
    INVALID_CREATE_STORE_MANUFACTURER(1029, "EVM Staff does not have store", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_UPDATE_STORE_MANUFACTURER(1030, "Cannot update store of EVM Staff", HttpStatus.FORBIDDEN),
    INVALID_NUMBER(1031, "The value of this field must be greater than 0", HttpStatus.BAD_REQUEST),
    MODEL_COLOR_EXISTED(1032, "Model màu đã tồn tại", HttpStatus.BAD_REQUEST),
    CONTRACT_NOT_FOUND(1033, "Hợp đồng không tồn tại", HttpStatus.NOT_FOUND),
    CONTRACT_FILE_URL_EXISTED(1034, "URL file hợp đồng đã tồn tại", HttpStatus.BAD_REQUEST),
    APPOINTMENT_NOT_FOUND(1035, "Lịch hẹn không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_TIME_RANGE(1036, "Thời gian không hợp lệ", HttpStatus.BAD_REQUEST),
    START_TIME_AFTER_END_TIME(1037, "Thời gian bắt đầu phải trước thời gian kết thúc", HttpStatus.BAD_REQUEST),
    PAST_APPOINTMENT_TIME(1038, "Thời gian lịch hẹn phải trong tương lai", HttpStatus.BAD_REQUEST),
    FEEDBACK_NOT_FOUND(1039, "Phản hồi không tồn tại", HttpStatus.NOT_FOUND),
    FEEDBACK_DETAIL_NOT_FOUND(1040, "Chi tiết phản hồi không tồn tại", HttpStatus.NOT_FOUND),
    TEST_DRIVE_CONFIG_NOT_FOUND(1041, "Cấu hình lái thử không tồn tại", HttpStatus.NOT_FOUND),
    INVENTORY_TRANSACTION_NOT_FOUND(1042, "Giao dịch tồn kho không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_NOT_EDITABLE(1043, "Đơn hàng không thể chỉnh sửa", HttpStatus.BAD_REQUEST),
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
