package ro.ubbcluj.cs.ams.student.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import ro.ubbcluj.cs.ams.student.dto.*;
import ro.ubbcluj.cs.ams.student.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.student.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.student.service.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {

    private final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private Service service;

    @Autowired
    private ServicesHealthChecker servicesHealthChecker;

    @Autowired
    private HandleServicesHealthRequests handleServicesHealthRequests;

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

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

    @RequestMapping(value = "/groups", method = RequestMethod.GET, params = {"specId"})
    public ResponseEntity<SGroups> findGroupsForSpecId(@RequestParam(name = "specId") Integer specId) {

        LOGGER.info("========== LOGGING findGroupsForSpecId ==========");
        LOGGER.info("Specialization id:        {}", specId);

        SGroups groups = service.findAllGroupsBySpecId(specId);

        LOGGER.info("========== SUCCESSFULLY LOGGING findGroupsForSpecId ==========");
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, params = {"groupId", "courseId"})
    public ResponseEntity<StudentsDetailsDto> findAllStudentsByCourseAndGroupId(@RequestHeader("Authorization") String authorization, @RequestParam(name = "groupId") Integer groupId, @RequestParam(name = "courseId") String courseId, Principal principal) {

        LOGGER.info("========== LOGGING findAllStudentsByCourseAndGroupId ==========");
        LOGGER.info("Group id:        {}", groupId);

        StudentsDto studentsDto = service.findAllStudentsByCourseAndGroupId(courseId, groupId);

        StudentsDetailsDto studentsDetailsDto = getStudentsDetails(studentsDto, authorization);

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllStudentsByCourseAndGroupId ==========");
        return new ResponseEntity<>(studentsDetailsDto, HttpStatus.OK);
    }

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

    private StudentsDetailsDto getStudentsDetails(StudentsDto studentsDto, String authorization) {

        List<StudentDetailsDto> studentDetailsDtos = new ArrayList<>();
        studentsDto.getData().forEach(student -> {

            StudentDetailsDto studentDetailsDto = webClientBuilder
                    .build()
                    .get()
                    .uri("http://auth-service/auth/users?username=" + student.getUserId())
                    .header("Authorization", authorization)
                    .retrieve()
                    .bodyToMono(StudentDetailsDto.class)
                    .block();
            studentDetailsDto.setGroup(service.findGroupById(student.getGroupId()));
            studentDetailsDtos.add(studentDetailsDto);
        });

        return StudentsDetailsDto.builder()
                .data(studentDetailsDtos).build();
    }

}
