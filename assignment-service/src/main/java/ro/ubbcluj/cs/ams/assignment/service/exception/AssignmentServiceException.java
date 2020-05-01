package ro.ubbcluj.cs.ams.assignment.service.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AssignmentServiceException extends RuntimeException {

    private AssignmentServiceExceptionType type;
    private HttpStatus status;

    public AssignmentServiceException(String msg, AssignmentServiceExceptionType type, HttpStatus status) {

        super(msg);
        this.type = type;
        this.status = status;
    }
}
