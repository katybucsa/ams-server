package ro.ubbcluj.cs.ams.course.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CourseServiceException extends RuntimeException {

    private CourseExceptionType type;
    private HttpStatus httpStatus;

    public CourseServiceException() {
    }

    public CourseServiceException(String message, CourseExceptionType type, HttpStatus httpStatus) {

        super(message);
        this.type = type;
        this.httpStatus = httpStatus;
    }

    public CourseServiceException(String message, Throwable cause, CourseExceptionType type, HttpStatus httpStatus) {

        super(message, cause);
        this.type = type;
        this.httpStatus = httpStatus;
    }
}
