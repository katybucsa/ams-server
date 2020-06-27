package ro.ubbcluj.cs.ams.assignment.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import ro.ubbcluj.cs.ams.assignment.dto.*;
import ro.ubbcluj.cs.ams.assignment.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.assignment.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.service.Service;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceException;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceExceptionType;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

    private static final Logger LOGGER = LogManager.getLogger(AssignmentController.class);

    @RequestMapping(value = "/health", method = RequestMethod.POST, params = {"service-name"})
    public void health(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Health check from service: {} ==========", serviceName);

        handleServicesHealthRequests.sendResponseToService(serviceName);
    }

    @RequestMapping(value = "/present", method = RequestMethod.POST, params = {"service-name"})
    public void present(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Service {} is alive ==========", serviceName);
        servicesHealthChecker.addService(serviceName);
    }

    @RequestMapping(value = "/running", method = RequestMethod.GET)
    public ResponseEntity running() {

        LOGGER.info("========== Service running ==========");
        return new ResponseEntity(HttpStatus.OK);
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
        if (null == microserviceCall.checkIfProfessorTeachesSpecificActivityTypeForASubject(gradeDto.getCourseId(), gradeDto.getTypeId(), teacherUsername)) {
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
    @RequestMapping(value = "/grades", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"courseId"})
    public ResponseEntity<?> findAllGradesByStudentAndCourseId(@RequestHeader(name = "Authorization") String authorization, @RequestParam(name = "courseId") String courseId, Principal principal) {

        LOGGER.info("========== LOGGING findAllGradesByStudentAndCourseId ==========");

        String studentUsername = principal.getName();
        List<Grade> grades = service.findAllGradesByStudentAndCourseId(studentUsername, courseId);
        CourseNameResponse courseNameResponse = findCourseById(courseId, authorization);
        GradesResponseDto gradesResponseDto = buildGradesResponse(grades, courseNameResponse, authorization);

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllGradesByStudentAndCourseId ==========");
        return new ResponseEntity<>(gradesResponseDto, HttpStatus.OK);
    }

    private CourseNameResponse findCourseById(String courseId, String authorization) {

        return webClientBuilder
                .build()
                .get()
                .uri("http://course-service/course/courses?courseId=" + courseId)
                .header("Authorization", authorization)
                .retrieve()
                .bodyToMono(CourseNameResponse.class)
                .block();
    }

    private GradesResponseDto buildGradesResponse(List<Grade> grades, CourseNameResponse courseNameResponse, String authorization) {

        List<StudentGrade> studentGrades = new ArrayList<>();
        Map<Integer, ActivityType> map = new HashMap<>();

        grades.forEach(grade -> {
            if (!map.containsKey(grade.getTypeId())) {
                ActivityType activityType = webClientBuilder
                        .build()
                        .get()
                        .uri("http://course-service/course/activity-type?typeId=" + grade.getTypeId())
                        .header("Authorization", authorization)
                        .retrieve()
                        .bodyToMono(ActivityType.class)
                        .block();
                map.put(activityType.getTypeId(), activityType);
            }
            StudentGrade studentGrade = StudentGrade.builder()
                    .course(courseNameResponse.getName())
                    .activityType(map.get(grade.getTypeId()).getName())
                    .value(grade.getValue())
                    .build();
            studentGrades.add(studentGrade);
        });

        return GradesResponseDto.builder()
                .data(studentGrades)
                .build();
    }
}
