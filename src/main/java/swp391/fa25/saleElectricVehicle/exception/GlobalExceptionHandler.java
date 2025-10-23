package swp391.fa25.saleElectricVehicle.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<String> handlingRuntimeException(RuntimeException runtimeException) {
        return ResponseEntity.badRequest().body(runtimeException.getMessage());
    }

    // GlobalExceptionHandler.java (Sửa lại để nó tạo ra đối tượng ApiResponse lỗi)

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException appException) {
        // Lấy ErrorCode từ AppException
        ErrorCode errorCode = appException.getErrorCode();

        // Xây dựng đối tượng ApiResponse lỗi
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .code(errorCode.getCode()) // Lấy mã lỗi nội bộ (1002, 1003,...)
                .message(errorCode.getMessage())
//                .data(appException.getValidationData())
                .build();

        // Trả về ResponseEntity với HTTP Status code (ví dụ: 400 Bad Request)
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(errorResponse);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Kích thước tệp vượt quá giới hạn cho phép!");
    }
}
