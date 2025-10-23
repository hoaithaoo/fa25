package swp391.fa25.saleElectricVehicle.exception;

import lombok.Builder;

public class AppException extends RuntimeException {
    private ErrorCode errorCode;
//    private Object validationData; // Có thể là DTO hoặc List<DTO> (error)

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

//    public AppException(ErrorCode errorCode, String message, Object validationData) {
//        super(message);
//        this.errorCode = errorCode;
//        this.validationData = validationData;
//    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

//    public Object getValidationData() {
//        return validationData;
//    }
}
