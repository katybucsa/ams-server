package ro.ubbcluj.cs.ams.subject.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SubjectServiceException extends RuntimeException {

    private SubjectExceptionType type;
    private HttpStatus httpStatus;

    public SubjectServiceException() {
    }

    public SubjectServiceException(String message, SubjectExceptionType type, HttpStatus httpStatus) {

        super(message);
        this.type = type;
        this.httpStatus = httpStatus;
    }

    public SubjectServiceException(String message, Throwable cause, SubjectExceptionType type, HttpStatus httpStatus) {

        super(message, cause);
        this.type = type;
        this.httpStatus = httpStatus;
    }
}
