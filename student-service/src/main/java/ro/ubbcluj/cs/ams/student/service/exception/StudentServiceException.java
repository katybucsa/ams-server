package ro.ubbcluj.cs.ams.student.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StudentServiceException extends RuntimeException {

    private StudentExceptionType type;
    private HttpStatus httpStatus;

    public StudentServiceException() {
    }

    public StudentServiceException(String message, StudentExceptionType type, HttpStatus httpStatus) {

        super(message);
        this.type = type;
        this.httpStatus = httpStatus;
    }

    public StudentServiceException(String message, Throwable cause, StudentExceptionType type, HttpStatus httpStatus) {

        super(message, cause);
        this.type = type;
        this.httpStatus = httpStatus;
    }
}
