package ro.ubbcluj.cs.ams.assignment.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.cs.ams.assignment.dto.GradeDto;
import ro.ubbcluj.cs.ams.assignment.dto.GradeResponseDto;
import ro.ubbcluj.cs.ams.assignment.dto.GradesResponseDto;
import ro.ubbcluj.cs.ams.assignment.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.assignment.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.assignment.service.Service;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceException;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceExceptionType;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AssignmentController {

    @Autowired
    private Service service;

    @Autowired
    private MicroserviceCall microserviceCall;

    @Autowired
    private ServicesHealthChecker servicesHealthChecker;

    @Autowired
    private HandleServicesHealthRequests handleServicesHealthRequests;

    private static final Logger LOGGER = LogManager.getLogger(AssignmentController.class);

    @RequestMapping(value = "/health", method = RequestMethod.POST, params = {"service-name"})
    public void health(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Health check from service: {} ", serviceName);

        handleServicesHealthRequests.sendResponseToService(serviceName);
    }

    @RequestMapping(value = "/present", method = RequestMethod.POST, params = {"service-name"})
    public void present(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Service {} is alive", serviceName);
        servicesHealthChecker.addService(serviceName);
    }

    @ApiOperation(value = "Assign given grade to the specified student")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = GradeResponseDto.class),
            @ApiResponse(code = 400, message = "INVALID_GRADE", response = AssignmentServiceExceptionType.class),
    })
    @RequestMapping(value = "/grades", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignGrade(@RequestBody @Valid GradeDto gradeDto, BindingResult result, Principal principal) {

        LOGGER.info("========== LOGGING assignGrade ==========");
        LOGGER.info("GradeDto {}", gradeDto);
        if (result.hasErrors()) {
            LOGGER.error("Invalid json object!");
            throw new AssignmentServiceException("Invalid grade" + gradeDto, AssignmentServiceExceptionType.INVALID_GRADE, HttpStatus.BAD_REQUEST);
        }

        String teacherUsername = principal.getName();
        if (null == microserviceCall.checkIfProfessorTeachesSpecificActivityTypeForASubject(gradeDto.getSubjectId(), gradeDto.getTypeId(), teacherUsername)) {
            LOGGER.error("========== subject microservice not responding ==========");
            return new ResponseEntity<>("This service is not currently available", HttpStatus.SERVICE_UNAVAILABLE);
        }
//        microserviceCall.checkIfStudentExistsAndIsEnrolledIntoSubject(gradeDto.getStudent(), gradeDto.getSubjectId(), "assignGrade");
        GradeResponseDto gradeResponseDto = service.addGrade(gradeDto, teacherUsername);

        LOGGER.info("========== SUCCESSFUL LOGGING assignGrade ==========");
        return new ResponseEntity<>(gradeResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all grades for a student to a specified subject")
    @ApiResponses(value = {
    })
    @RequestMapping(value = "/grades", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllGradesByStudentAndSubjectId(@RequestParam(name = "subjectId") String subjectId, Principal principal) {

        LOGGER.info("========== LOGGING findAllGradesByStudentAndSubjectId ==========");

        String studentUsername = principal.getName();
        GradesResponseDto gradesResponseDto = service.findAllGradesByStudentAndSubjectId(studentUsername, subjectId);

        LOGGER.info("========== SUCCESSFUL LOGGING findAllGradesByStudentAndSubjectId ==========");
        return new ResponseEntity<>(gradesResponseDto, HttpStatus.OK);
    }
}
