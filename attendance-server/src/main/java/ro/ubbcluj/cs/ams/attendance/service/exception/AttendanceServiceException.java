package ro.ubbcluj.cs.ams.attendance.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AttendanceServiceException extends RuntimeException {

    private AttendanceExceptionType type;
    private HttpStatus httpStatus;

    public AttendanceServiceException() {
    }

    public AttendanceServiceException(String message, AttendanceExceptionType type, HttpStatus httpStatus) {

        super(message);
        this.type = type;
        this.httpStatus = httpStatus;
    }

    public AttendanceServiceException(String message, Throwable cause, AttendanceExceptionType type, HttpStatus httpStatus) {

        super(message, cause);
        this.type = type;
        this.httpStatus = httpStatus;
    }
}
