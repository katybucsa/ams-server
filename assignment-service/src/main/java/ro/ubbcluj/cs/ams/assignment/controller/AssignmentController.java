package ro.ubbcluj.cs.ams.assignment.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ro.ubbcluj.cs.ams.assignment.dto.grade.GradeDto;
import ro.ubbcluj.cs.ams.assignment.dto.grade.GradesResponseDto;
import ro.ubbcluj.cs.ams.assignment.dto.responses.ActivityType;
import ro.ubbcluj.cs.ams.assignment.dto.responses.CourseNameResponse;
import ro.ubbcluj.cs.ams.assignment.dto.responses.CpLinkBean;
import ro.ubbcluj.cs.ams.assignment.dto.responses.StudentGrade;
import ro.ubbcluj.cs.ams.assignment.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.assignment.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.service.Service;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceException;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceExceptionType;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@RestController
@EnableWebMvc
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

    @Resource
    private HttpServletRequest httpServletRequest;

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
            @ApiResponse(code = 200, message = "SUCCESS", response = Grade.class),
            @ApiResponse(code = 400, message = "INVALID_GRADE", response = AssignmentServiceExceptionType.class),
            @ApiResponse(code = 404, message = "TEACHER_NOT_FOUND", response = AssignmentServiceExceptionType.class),
    })
    @RequestMapping(value = "/grades", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Grade> assignGrade(@RequestBody @Valid GradeDto gradeDto, BindingResult result, Principal principal) {

        LOGGER.info("========== LOGGING assignGrade ==========");
        LOGGER.info("GradeDto {}", gradeDto);
        if (result.hasErrors()) {
            LOGGER.error("Invalid json object!");
            throw new AssignmentServiceException("Invalid grade", AssignmentServiceExceptionType.INVALID_GRADE, HttpStatus.BAD_REQUEST);
        }

        String teacherUsername = principal.getName();
        CpLinkBean cpLinkBean = microserviceCall.checkIfProfessorTeachesSpecificActivityTypeForASubject(gradeDto.getCourseId(), gradeDto.getTypeId(), teacherUsername);
        if (null == cpLinkBean) {
            LOGGER.error("========== subject microservice not responding ==========");
            throw new AssignmentServiceException("There is no link for professor and course", AssignmentServiceExceptionType.ERROR, HttpStatus.NOT_FOUND);
        }
        Grade grade = service.addGrade(gradeDto, teacherUsername);

        LOGGER.info("========== SUCCESSFUL LOGGING assignGrade ==========");
        return new ResponseEntity<>(grade, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all grades for a student to a specified subject")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = Grade.class),

    })
    @RequestMapping(value = "/grades", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"courseId"})
    public ResponseEntity<GradesResponseDto> findAllGradesByStudentAndCourseId(@RequestHeader(name = "Authorization") String authorization, @RequestParam(name = "courseId") String courseId, Principal principal) {

        LOGGER.info("========== LOGGING findAllGradesByStudentAndCourseId ==========");

        String studentUsername = principal.getName();
        List<Grade> grades = service.findAllGradesByStudentAndCourseId(studentUsername, courseId);
        CourseNameResponse courseNameResponse = findCourseById(courseId);
        if (Objects.isNull(courseNameResponse))
            throw new AssignmentServiceException("Course not found", AssignmentServiceExceptionType.ERROR, HttpStatus.NOT_FOUND);
        if (Objects.isNull(courseNameResponse.getName())) {
            courseNameResponse = getFromCacheCourseNameResponse(courseId, courseNameResponse);
            if (Objects.isNull(courseNameResponse))
                throw new AssignmentServiceException("Could not retrieve course", AssignmentServiceExceptionType.ERROR, HttpStatus.SERVICE_UNAVAILABLE);
        }
        GradesResponseDto gradesResponseDto = buildGradesResponse(grades, courseNameResponse);

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllGradesByStudentAndCourseId ==========");
        return new ResponseEntity<>(gradesResponseDto, HttpStatus.OK);
    }

    @Retryable(value = {WebClientResponseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 6000))
    @CachePut(value = "courseNameResponse", key = "#courseId")
    public CourseNameResponse findCourseById(String courseId) {

        return webClientBuilder
                .build()
                .get()
                .uri("http://course-service/course/courses?courseId=" + courseId)
                .header("Authorization", httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .bodyToMono(CourseNameResponse.class)
                .block();
    }

    @Cacheable(value = "courseNameResponse", key = "#courseId", sync = true)
    public CourseNameResponse getFromCacheCourseNameResponse(String courseId, CourseNameResponse courseNameResponse) {

        return courseNameResponse;
    }

    @Retryable(value = {WebClientResponseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 6000))
    @CachePut(value = "gradesResponseDto", key = "#grades")
    public GradesResponseDto buildGradesResponse(List<Grade> grades, CourseNameResponse courseNameResponse) {

        List<StudentGrade> studentGrades = new ArrayList<>();
        Map<Integer, ActivityType> map = new HashMap<>();
        grades.forEach(grade -> {
            if (!map.containsKey(grade.getTypeId())) {
                ActivityType activityType = webClientBuilder
                        .build()
                        .get()
                        .uri("http://course-service/course/activity-type?typeId=" + grade.getTypeId())
                        .header("Authorization", httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
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

    @Recover
    public CpLinkBean recover(WebClientResponseException t) {

        LOGGER.info("Recover");
        throw new AssignmentServiceException("Could not assign grade!", AssignmentServiceExceptionType.ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }


    @ExceptionHandler({AssignmentServiceException.class})
    public ResponseEntity<AssignmentServiceExceptionType> handleException(AssignmentServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getStatus());
    }
}
