package ro.ubbcluj.cs.ams.course.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ro.ubbcluj.cs.ams.course.controller.CourseController;
import ro.ubbcluj.cs.ams.course.dto.activityType.ActivityTypes;
import ro.ubbcluj.cs.ams.course.service.Mappers;
import ro.ubbcluj.cs.ams.course.dto.StudentDetailsDto;
import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoRequest;
import ro.ubbcluj.cs.ams.course.dto.course.CourseNameResponse;
import ro.ubbcluj.cs.ams.course.dto.course.CourseResponseDto;
import ro.ubbcluj.cs.ams.course.dto.course.CoursesDto;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipantsResponseDto;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipationDetalis;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipationsResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostsResponseDto;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.*;
import ro.ubbcluj.cs.ams.course.model.tables.records.*;
import ro.ubbcluj.cs.ams.course.repository.activityTypes.ActivityTypeRepo;
import ro.ubbcluj.cs.ams.course.repository.courseCodeRepo.CourseCodeRepo;
import ro.ubbcluj.cs.ams.course.repository.courseRepo.CourseRepo;
import ro.ubbcluj.cs.ams.course.repository.cpLinkRepo.CpLinkRepo;
import ro.ubbcluj.cs.ams.course.repository.event.EventRepo;
import ro.ubbcluj.cs.ams.course.repository.participationRepo.ParticipationRepo;
import ro.ubbcluj.cs.ams.course.repository.postRepo.PostRepo;
import ro.ubbcluj.cs.ams.course.repository.specializationRepo.SpecializationRepo;
import ro.ubbcluj.cs.ams.course.service.Service;
import ro.ubbcluj.cs.ams.course.service.exception.CourseExceptionType;
import ro.ubbcluj.cs.ams.course.service.exception.CourseServiceException;

import javax.annotation.Resource;
import javax.inject.Provider;
import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class CourseServiceImpl implements Service {

    private final Logger LOGGER = LogManager.getLogger(CourseController.class);

    @Autowired
    private SpecializationRepo specializationDao;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private CourseCodeRepo courseCodeRepo;

    @Autowired
    private CpLinkRepo cpLinkRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private ActivityTypeRepo activityTypeRepo;

    @Autowired
    private ParticipationRepo participRepo;

    @Autowired
    private Mappers mappers;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Provider<Instant> instant;

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

    @Resource
    private HttpServletRequest httpServletRequest;

    private String prefixCode = "ML";

    private DecimalFormat myFormat = new DecimalFormat("0000");

    @Transactional
    @Override
    public CourseResponseDto addCourse(CourseDtoRequest courseRequestDto) {

        LOGGER.info("========== LOGGING addCourse ==========");

        Course course = new Course("", courseRequestDto.getName(), courseRequestDto.getCredits(), courseRequestDto.getSpecId(), courseRequestDto.getYear());
        String courseId = generateCourseCode(course);
        course.setId(courseId);

        CourseRecord savedCourse = courseRepo.addCourse(course);

        if (savedCourse == null)
            throw new CourseServiceException("Course \"" + course.getName() + "\" already exists!!", CourseExceptionType.DUPLICATE_COURSE, HttpStatus.BAD_REQUEST);

        LOGGER.info("========== SUCCESSFUL LOGGING addCourse ==========");
        return CourseResponseDto.builder()
                .id(savedCourse.getId())
                .name(savedCourse.getName())
                .credits(savedCourse.getCredits())
                .specId(savedCourse.getSpecId())
                .year(savedCourse.getYear())
                .build();
    }

    @Override
    public CpLink findCpLink(String courseId, int type, String professor) {

        LOGGER.info("========== LOGGING findCpLink ==========");

        CpLinkRecord cpLinkRecord = cpLinkRepo.findCpLink(courseId, type, professor);

        LOGGER.info("========== SUCCESSFULLY LOGGING findSpLink ==========");
        return mappers.cpLinkRecordToCpLink(cpLinkRecord);
    }

    @Override
    public CoursesDto findAllCoursesByProfessorUsername(String professorUsername) {

        LOGGER.info("========== LOGGING findAllCoursesByProfessorUsername ==========");

        List<String> coursesIds = cpLinkRepo.findAllLinksByProfessorUsername(professorUsername)
                .stream()
                .map(CpLinkRecord::getCourseId)
                .collect(Collectors.toList());
        List<CourseRecord> courses = courseRepo.findAllCoursesByIds(coursesIds);

        LOGGER.info("========== SUCCESSFUL LOGGING findAllCoursesByProfessorUsername ==========");
        return CoursesDto.builder()
                .data(mappers.coursesRecordToCourseDtoResponse(courses))
                .build();
    }

    @SneakyThrows
    @Override
    public PostResponseDto addPost(PostRequestDto postRequestDto, String professorUsername) {

        LOGGER.info("========== LOGGING addPost ==========");

        Post post = mappers.postRequestDtoToPost(postRequestDto);
        post.setDate(LocalDateTime.ofInstant(instant.get(), ZoneId.systemDefault()));
        post.setProfessorId(professorUsername);
        PostRecord postRecord = postRepo.addPost(post);
        PostResponseDto postResponseDto = mappers.postRecordToPostResponseDto(postRecord);

        if (post.getType().equals("event")) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                    postRequestDto.getEvent().getDate()), ZoneId.systemDefault());
            Event event = new Event(postRecord.getId(), dateTime.toLocalDate(), dateTime.toLocalTime(), postRequestDto.getEvent().getPlace());
            CourseRecord courseRecord = courseRepo.findById(postResponseDto.getCourseId());
            eventRepo.addEvent(event);
            postResponseDto.setEvent(event);
            postResponseDto.setCourseName(courseRecord.getName());
        }
        jmsTemplate.convertAndSend("notification-queue", objectMapper.writeValueAsString(postResponseDto));

        LOGGER.info("========== SUCCESSFUL LOGGING addPost ==========");
        return postResponseDto;
    }

    @Override
    public PostsResponseDto findPostsByCourseId(String courseId) {

        LOGGER.info("========== LOGGING findPostsByCourseId ==========");

        List<PostRecord> postRecords = postRepo.findPostsByCourseId(courseId);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        postRecords.forEach(postRecord -> {
            PostResponseDto postResponseDto = mappers.postRecordToPostResponseDto(postRecord);
            postResponseDto.setDate(postRecord.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            if (postRecord.getType().equals("event")) {
                EventRecord eventRecord = eventRepo.findById(postRecord.getId());
                postResponseDto.setEvent(mappers.eventRecordToEvent(eventRecord));
            }
            postResponseDtos.add(postResponseDto);
        });
        PostsResponseDto postsResponseDto = PostsResponseDto.builder()
                .data(postResponseDtos)
                .build();

        LOGGER.info("========== SUCCESSFUL LOGGING findPostsByCourseId ==========");
        return postsResponseDto;
    }

    @SneakyThrows
    @Override
    public Participation addOrDeleteParticipation(Participation participation) {

        LOGGER.info("========== LOGGING addParticipation ==========");

        int inserted = participRepo.addOrDeleteParticipation(participation);
        if (inserted != 0) {
            PostRecord postRecord = postRepo.findById(participation.getEventId());
            CourseRecord courseRecord = courseRepo.findById(postRecord.getCourseId());
            List<String> participants = participRepo
                    .findAllByEventId(participation.getEventId())
                    .stream()
                    .map(ParticipationRecord::getUserId)
                    .collect(Collectors.toList());
            Map<String, String> names = new HashMap<>();
            participants.forEach(p -> {
                if (!names.containsKey(p)) {
                    String n = findUserNameByUserId(p);
                    names.put(p, n);
                }
            });

            ParticipationDetalis pDetails = ParticipationDetalis.builder()
                    .courseId(postRecord.getCourseId())
                    .courseName(courseRecord.getName())
                    .postId(postRecord.getId())
                    .postTitle(postRecord.getTitle())
                    .professorId(postRecord.getProfessorId())
                    .participants(new ArrayList<>(names.values()))
                    .build();

            jmsTemplate.convertAndSend("particip-queue", objectMapper.writeValueAsString(pDetails));
        }

        LOGGER.info("========== SUCCESSFUL LOGGING addParticipation ==========");
        return participation;
    }

    @Override
    public ParticipationsResponseDto findParticipationsByUserId(String userId) {

        LOGGER.info("========== LOGGING findParticipationsByUserId ==========");

        List<ParticipationRecord> participationRecords = participRepo.findAllByUserId(userId);

        LOGGER.info("========== SUCCESSFUL LOGGING findParticipationsByUserId ==========");
        return ParticipationsResponseDto.builder()
                .data(participationRecords
                        .stream()
                        .map(ParticipationRecord::getEventId)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public ActivityTypes findAllActivityTypes() {

        LOGGER.info("========== LOGGING findAllActivityTypes ==========");

        List<ActivityType> activityTypes = mappers.activityTypesRecordToActivityTypes(activityTypeRepo.findActivityTypes());

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllActivityTypes ==========");
        return ActivityTypes.builder()
                .data(activityTypes)
                .build();
    }

    @Override
    public CourseNameResponse findCourseById(String courseId) {

        LOGGER.info("========== LOGGING findCourseById ==========");

        CourseRecord courseRecord = courseRepo.findById(courseId);
        if (Objects.isNull(courseRecord)) {

            LOGGER.warn("========== course with id {} was not found ==========", courseId);
            throw new CourseServiceException("Course not found", CourseExceptionType.COURSE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        LOGGER.info("========== SUCCESSFULLY LOGGING findCourseById ==========");
        return mappers.courseRecordToCourseNameResponse(courseRecord);
    }

    @Override
    public ActivityType findActivityTypeById(int typeId) {

        LOGGER.info("========== LOGGING findActivityTypeById ==========");

        ActivityTypeRecord activityTypeRecord = activityTypeRepo.findActivityTypeById(typeId);
        if (Objects.isNull(activityTypeRecord)) {

            LOGGER.warn("========== activity with id {} was not found ==========", typeId);
            throw new CourseServiceException("Activity type not found", CourseExceptionType.ACTIVITY_TYPE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        ActivityType activityType = mappers.activityTypeRecordToActivityType(activityTypeRecord);

        LOGGER.info("========== SUCCESSFULLY LOGGING findActivityTypeById ==========");
        return activityType;
    }

    @Override
    public ParticipantsResponseDto findEventParticipants(Integer postId, String auth) {

        LOGGER.info("========== LOGGING findEventParticipants ==========");

        List<ParticipationRecord> participationRecords = participRepo.findAllByEventId(postId);
        if (Objects.isNull(participationRecords)) {

            LOGGER.warn("========== Post with id {} was not found ==========", postId);
            throw new CourseServiceException("Post was not found", CourseExceptionType.POST_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        List<String> list = new ArrayList<>();
        participationRecords.forEach(p -> {
            list.add(findUserNameByUserId(p.getUserId()));
        });

        LOGGER.info("========== SUCCESSFULLY LOGGING findEventParticipants ==========");
        return ParticipantsResponseDto.builder()
                .data(list)
                .build();
    }

    private String generateCourseCode(Course course) {

        SpecializationRecord specialization = specializationDao.findById(course.getSpecId());
        if (Objects.isNull(specialization)) {
            LOGGER.info("Specialization not found!");
            throw new CourseServiceException("Specialization not found!", CourseExceptionType.SPECIALIZATION_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        String code = prefixCode + specialization.getLanguage().charAt(0);

        CourseCodeRecord scr = courseCodeRepo.findByCourseName(course.getName());

        if (scr == null) { // this subject does not exist

            Integer intCode = courseCodeRepo.addCourseCode(new CourseCode(null, course.getName()));
            String stringCode = myFormat.format(intCode);
            code += stringCode;
        } else {
            code += scr.getCode();
        }
        return code;
    }

    @Retryable(value = {WebClientResponseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 6000))
    @Cacheable(value = "userNameById", key = "#userId")
    public String findUserNameByUserId(String userId) {

        StudentDetailsDto studentDetailsDto = webClientBuilder
                .build()
                .get()
                .uri("http://auth-service/auth/users?username=" + userId)
                .header("Authorization", httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .bodyToMono(StudentDetailsDto.class)
                .block();
        return Objects.requireNonNull(studentDetailsDto).getFirstName() + " " + studentDetailsDto.getLastName();
    }

    @Recover
    public String recover(WebClientResponseException t) {

        LOGGER.info("Recover");
        throw new CourseServiceException("Could not get users!", CourseExceptionType.ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
