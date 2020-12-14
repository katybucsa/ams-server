package ro.ubbcluj.cs.ams.student.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ro.ubbcluj.cs.ams.student.dto.student.EnrolledStudents;
import ro.ubbcluj.cs.ams.student.dto.student.StudentDetailsDto;
import ro.ubbcluj.cs.ams.student.dto.student.StudentsDetailsDto;
import ro.ubbcluj.cs.ams.student.dto.student.StudentsDto;
import ro.ubbcluj.cs.ams.student.dto.group.SGroups;
import ro.ubbcluj.cs.ams.student.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.student.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.student.service.Service;
import ro.ubbcluj.cs.ams.student.service.exception.StudentExceptionType;
import ro.ubbcluj.cs.ams.student.service.exception.StudentServiceException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private Service service;

    @Autowired
    private ServicesHealthChecker servicesHealthChecker;

    @Autowired
    private HandleServicesHealthRequests handleServicesHealthRequests;

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

    @Resource
    private HttpServletRequest httpServletRequest;

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

    @RequestMapping(value = "/running", method = RequestMethod.GET)
    public ResponseEntity running() {

        LOGGER.info("========== Service running ==========");
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "Find all groups for a specialization")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = SGroups.class)
    })
    @RequestMapping(value = "/groups", method = RequestMethod.GET, params = {"specId"})
    public ResponseEntity<SGroups> findGroupsForSpecId(@RequestParam(name = "specId") Integer specId) {

        LOGGER.info("========== LOGGING findGroupsForSpecId ==========");
        LOGGER.info("Specialization id:        {}", specId);

        SGroups groups = service.findAllGroupsBySpecId(specId);

        LOGGER.info("========== SUCCESSFULLY LOGGING findGroupsForSpecId ==========");
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all students from a group enrolled into a specific course")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = StudentsDetailsDto.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, params = {"groupId", "courseId"})
    public ResponseEntity<StudentsDetailsDto> findAllStudentsByCourseAndGroupId(@RequestParam(name = "groupId") Integer groupId, @RequestParam(name = "courseId") String courseId, Principal principal) {

        LOGGER.info("========== LOGGING findAllStudentsByCourseAndGroupId ==========");
        LOGGER.info("Group id: {}", groupId);

        StudentsDto studentsDto = service.findAllStudentsByCourseAndGroupId(courseId, groupId);

        StudentsDetailsDto studentsDetailsDto = getStudentsDetails(studentsDto);

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllStudentsByCourseAndGroupId ==========");
        return new ResponseEntity<>(studentsDetailsDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all enrollemnts a specific course")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = EnrolledStudents.class)
    })
    @RequestMapping(value = "/enrollment", method = RequestMethod.GET, params = {"courseId"})
    public ResponseEntity<EnrolledStudents> findAllCourseEnrollments(@RequestParam(name = "courseId") String courseId) {

        LOGGER.info("========== LOGGING findAllCourseEnrollments ==========");
        LOGGER.info("Course id:        {}", courseId);

        EnrolledStudents enrolledStudents = EnrolledStudents.builder()
                .data(service.findAllCourseEnrollments(courseId))
                .build();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllCourseEnrollments ==========");
        return new ResponseEntity<>(enrolledStudents, HttpStatus.OK);
    }

    @Retryable(value = {WebClientResponseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 6000))
    @CachePut(value = "studentsDetails", key = "#studentsDto")
    public StudentsDetailsDto getStudentsDetails(StudentsDto studentsDto) {

        List<StudentDetailsDto> studentDetailsDtos = new ArrayList<>();
        studentsDto.getData().forEach(student -> {

            StudentDetailsDto studentDetailsDto = webClientBuilder
                    .build()
                    .get()
                    .uri("http://auth-service/auth/users?username=" + student.getUserId())
                    .header("Authorization", httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                    .retrieve()
                    .bodyToMono(StudentDetailsDto.class)
                    .block();

            studentDetailsDto.setGroup(service.findGroupById(student.getGroupId()));
            studentDetailsDtos.add(studentDetailsDto);
        });

        return StudentsDetailsDto.builder()
                .data(studentDetailsDtos).build();
    }

    @Recover
    public StudentsDetailsDto recover(WebClientResponseException t) {

        LOGGER.info("Recover");
        throw new StudentServiceException("Could not get student details!", StudentExceptionType.ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({StudentServiceException.class})
    public ResponseEntity<StudentExceptionType> handleException(StudentServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }
}
