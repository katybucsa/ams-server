package ro.ubbcluj.cs.ams.notification.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotificationServiceException extends RuntimeException {

    private NotificationExceptionType type;
    private HttpStatus httpStatus;

    public NotificationServiceException() {
    }

    public NotificationServiceException(String message, NotificationExceptionType type, HttpStatus httpStatus) {

        super(message);
        this.type = type;
        this.httpStatus = httpStatus;
    }

    public NotificationServiceException(String message, Throwable cause, NotificationExceptionType type, HttpStatus httpStatus) {

        super(message, cause);
        this.type = type;
        this.httpStatus = httpStatus;
    }
}
