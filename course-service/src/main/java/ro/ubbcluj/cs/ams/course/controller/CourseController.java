package ro.ubbcluj.cs.ams.course.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.cs.ams.course.dto.activityType.ActivityTypes;
import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoRequest;
import ro.ubbcluj.cs.ams.course.dto.course.CourseNameResponse;
import ro.ubbcluj.cs.ams.course.dto.course.CourseResponseDto;
import ro.ubbcluj.cs.ams.course.dto.course.CoursesDto;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipantsResponseDto;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipationsResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostsResponseDto;
import ro.ubbcluj.cs.ams.course.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.course.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.course.model.tables.Course;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.ActivityType;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.CpLink;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Participation;
import ro.ubbcluj.cs.ams.course.service.Service;
import ro.ubbcluj.cs.ams.course.service.exception.CourseExceptionType;
import ro.ubbcluj.cs.ams.course.service.exception.CourseServiceException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;

@RestController
public class CourseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private Service service;

    @Autowired
    private ServicesHealthChecker servicesHealthChecker;

    @Autowired
    private HandleServicesHealthRequests handleServicesHealthRequests;


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

    @ApiOperation(value = "Add given course")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = Course.class),
            @ApiResponse(code = 400, message = "DUPLICATE_COURSE", response = CourseExceptionType.class),
    })
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseResponseDto> addCourse(@RequestBody @Valid CourseDtoRequest course, BindingResult result) {

        LOGGER.info("========== LOGGING addCourse ==========");
        loggingCourse(course);

        if (result.hasErrors())
            throw new CourseServiceException("Invalid course " + course, CourseExceptionType.INVALID_COURSE, HttpStatus.BAD_REQUEST);

        CourseResponseDto courseDtoResponse = service.addCourse(course);

        LOGGER.info("========== SUCCESSFUL LOGGING addCourse ==========");
        return new ResponseEntity<>(courseDtoResponse, HttpStatus.OK);
    }


    @ApiOperation(value = "Find CPLink by id - course id, activity id, professor username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = CpLink.class),
            @ApiResponse(code = 404, message = "NOT FOUND", response = CourseExceptionType.class)
    })
    @RequestMapping(value = "/assign", method = RequestMethod.GET, params = {"courseId", "activityTypeId", "professorUsername"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CpLink> findCpLink(@RequestParam(name = "courseId") String courseId, @RequestParam(name = "activityTypeId") Integer activityTypeId, @RequestParam(name = "professorUsername") String professorUsername) {

        LOGGER.info("========== LOGGING findSpLink ==========");
        LOGGER.info("CourseId {}, ActivityTypeId {}, ProfessorUsername {}", courseId, activityTypeId, professorUsername);

        CpLink cpLink = service.findCpLink(courseId, activityTypeId, professorUsername);
        if (Objects.isNull(cpLink))
            throw new CourseServiceException("Assignment not found", CourseExceptionType.ASSIGNMENT_NOT_FOUND, HttpStatus.NOT_FOUND);

        LOGGER.info("========== SUCCESSFULLY LOGGING findSpLink ==========");
        return new ResponseEntity<>(cpLink, HttpStatus.OK);
    }

    @ApiOperation(value = "Get all courses a professor is teaching")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = CoursesDto.class)
//            @ApiResponse(code = 404, message = "NOT FOUND", response = CourseExceptionType.class)
    })
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CoursesDto> findAllCoursesForProfessor(Principal principal) {

        LOGGER.info("========== LOGGING findAllCoursesForProfessor ==========");
        LOGGER.info("Professor username: {}", principal.getName());

        CoursesDto coursesDto = service.findAllCoursesByProfessorUsername(principal.getName());

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllCoursesForProfessor ==========");
        return new ResponseEntity<>(coursesDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Find posts by course id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = PostsResponseDto.class),
    })
    @RequestMapping(value = "/{courseId}/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostsResponseDto> findPostsByCourseId(@PathVariable(name = "courseId") String courseId) {

        LOGGER.info("========== LOGGING findPostsByCourseId ==========");
        LOGGER.info("Course id {}", courseId);

        PostsResponseDto posts = service.findPostsByCourseId(courseId);

        LOGGER.info("========== SUCCESSFULLY LOGGING findPostsByCourseId ==========");
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }


    @ApiOperation(value = "Add post to a course")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = PostResponseDto.class),

    })
    @RequestMapping(value = "/{courseId}/posts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponseDto> addPost(@RequestBody @Valid PostRequestDto postRequestDto, @PathVariable(name = "courseId") String courseId, BindingResult result, Principal principal) {

        LOGGER.info("========== LOGGING addPost ==========");
        LOGGER.info("PostRequestDto {}", postRequestDto);

        if (result.hasErrors()) {
            throw new CourseServiceException("All fields are required", CourseExceptionType.ACTIVITY_TYPE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        postRequestDto.setCourseId(courseId);
        PostResponseDto postRecord = service.addPost(postRequestDto, principal.getName());

        LOGGER.info("========== SUCCESSFULLY LOGGING addPost ==========");
        return new ResponseEntity<>(postRecord, HttpStatus.OK);
    }

    @ApiOperation(value = "Add participation to an event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = Participation.class),
    })
    @RequestMapping(value = "/{courseId}/events/participations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participation> addOrDeleteParticipation(@RequestBody Participation participation, Principal principal, @RequestHeader(name = "Authorization") String auth) {

        LOGGER.info("========== LOGGING addOrDeleteParticipation ==========");

        if (Objects.isNull(participation.getEventId())) {
            throw new CourseServiceException("Event id must not be null", CourseExceptionType.BAD_DATA, HttpStatus.BAD_REQUEST);
        }

        participation.setUserId(principal.getName());
        participation = service.addOrDeleteParticipation(participation);

        LOGGER.info("========== SUCCESSFULLY LOGGING addOrDeleteParticipation ==========");
        return new ResponseEntity<>(participation, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all events participations for user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = ParticipationsResponseDto.class)
    })
    @RequestMapping(value = "/{courseId}/events/participations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipationsResponseDto> findAllParticipationsByUserId(Principal principal) {

        LOGGER.info("========== LOGGING findAllParticipationsByUserId ==========");

        ParticipationsResponseDto participations = service.findParticipationsByUserId(principal.getName());

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllParticipationsByUserId ==========");
        return new ResponseEntity<>(participations, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all activity types")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = ActivityTypes.class)
    })
    @RequestMapping(value = "/activity-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActivityTypes> findAllActivityTypes() {

        LOGGER.info("========== LOGGING findAllActivityTypes ==========");

        ActivityTypes activityTypes = service.findAllActivityTypes();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllActivityTypes ==========");
        return new ResponseEntity<>(activityTypes, HttpStatus.OK);
    }

    @ApiOperation(value = "Find activity type by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = ActivityType.class),
            @ApiResponse(code = 404, message = "ACTIVITY_TYPE_NOT_FOUND", response = CourseExceptionType.class),
    })
    @RequestMapping(value = "/activity-type", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"typeId"})
    public ResponseEntity<ActivityType> findActivityTypeById(@RequestParam(name = "typeId") int typeId) {

        LOGGER.info("========== LOGGING findActivityTypeById ==========");
        LOGGER.info("========== activity type id {} ==========", typeId);


        ActivityType activityType = service.findActivityTypeById(typeId);

        LOGGER.info("========== SUCCESSFULLY LOGGING findActivityTypeById ==========");
        return new ResponseEntity<>(activityType, HttpStatus.OK);
    }

    @ApiOperation(value = "Find course by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = CourseNameResponse.class),
            @ApiResponse(code = 404, message = "COURSE_NOT_FOUND", response = CourseExceptionType.class)
    })
    @RequestMapping(value = "/courses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"courseId"})
    public ResponseEntity<CourseNameResponse> findCourseById(@RequestParam(name = "courseId") String courseId) {

        LOGGER.info("========== LOGGING findCourseById ==========");
        LOGGER.info("========== course id {} ==========", courseId);

        CourseNameResponse courseNameResponse = service.findCourseById(courseId);

        LOGGER.info("========== SUCCESSFULLY LOGGING findCourseById ==========");
        return new ResponseEntity<>(courseNameResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all event participations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = ParticipantsResponseDto.class),
            @ApiResponse(code = 404, message = "POST_NOT_FOUND", response = CourseExceptionType.class),
    })
    @RequestMapping(value = "/{courseId}/events/{postId}/participations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantsResponseDto> findEventParticipants(@PathVariable(name = "postId") Integer postId, @RequestHeader(name = "Authorization") String auth) {

        LOGGER.info("========== LOGGING findEventParticipants ==========");

        ParticipantsResponseDto participantsResponseDto = service.findEventParticipants(postId, auth);

        LOGGER.info("========== SUCCESSFULLY LOGGING findEventParticipants ==========");
        return new ResponseEntity<>(participantsResponseDto, HttpStatus.OK);
    }

    private void loggingCourse(CourseDtoRequest course) {

        LOGGER.info("Course name: {}", course.getName());
        LOGGER.info("Course credits: {}", course.getCredits());
        LOGGER.info("Course specialization: {}", course.getSpecId());
        LOGGER.info("Course year: {}", course.getYear());
    }

    @ExceptionHandler({CourseServiceException.class})
//    @ResponseBody
    public ResponseEntity<CourseExceptionType> handleException(CourseServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }
}
