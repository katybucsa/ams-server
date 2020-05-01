package ro.ubbcluj.cs.ams.assignment.controller;

import org.springframework.http.HttpStatus;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceException;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceExceptionType;

public class SentinelFallback {

    public static void linkVerificationFallback(String subjectId, int activityTypeId, String professorUsername) {

        System.out.println("Fallback subject service call");
        throw new AssignmentServiceException("Service unavailable!\n",
                AssignmentServiceExceptionType.TEACHER_ACTIVITY_TYPE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
