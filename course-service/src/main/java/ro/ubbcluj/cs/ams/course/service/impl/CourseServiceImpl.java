package ro.ubbcluj.cs.ams.course.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import ro.ubbcluj.cs.ams.course.controller.CourseController;
import ro.ubbcluj.cs.ams.course.dto.Mappers;
import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoRequest;
import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoResponse;
import ro.ubbcluj.cs.ams.course.dto.course.CoursesDto;
import ro.ubbcluj.cs.ams.course.dto.cplink.CpLinkResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostsResponseDto;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Course;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.CourseCode;
import ro.ubbcluj.cs.ams.course.model.tables.records.*;
import ro.ubbcluj.cs.ams.course.repository.courseCodeRepo.CourseCodeRepo;
import ro.ubbcluj.cs.ams.course.repository.courseRepo.CourseRepo;
import ro.ubbcluj.cs.ams.course.repository.cpLinkRepo.CpLinkRepo;
import ro.ubbcluj.cs.ams.course.repository.postRepo.PostRepo;
import ro.ubbcluj.cs.ams.course.repository.specializationRepo.SpecializationRepo;
import ro.ubbcluj.cs.ams.course.service.Service;
import ro.ubbcluj.cs.ams.course.service.exception.CourseExceptionType;
import ro.ubbcluj.cs.ams.course.service.exception.CourseServiceException;

import javax.jms.Queue;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
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
    private PostRepo postRepo;

    @Autowired
    private Mappers mappers;

    @Autowired
    private Queue notificationQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String prefixCode = "ML";

    private DecimalFormat myFormat = new DecimalFormat("0000");


    @Transactional
    @Override
    public CourseDtoResponse addCourse(CourseDtoRequest courseRequestDto) {

        LOGGER.info("========== LOGGING addCourse ==========");

        Course course = new Course("", courseRequestDto.getName(), courseRequestDto.getCredits(), courseRequestDto.getSpecId(), courseRequestDto.getYear());
        String courseId = generateCourseCode(course);
        course.setId(courseId);

        CourseRecord savedCourse = courseRepo.addCourse(course);

        if (savedCourse == null)
            throw new CourseServiceException("Course \"" + course.getName() + "\" already exists!!", CourseExceptionType.DUPLICATE_SUBJECT, HttpStatus.BAD_REQUEST);

        LOGGER.info("========== SUCCESSFUL LOGGING addCourse ==========");
        return CourseDtoResponse.builder()
                .id(savedCourse.getId())
                .name(savedCourse.getName())
                .credits(savedCourse.getCredits())
                .specId(savedCourse.getSpecId())
                .year(savedCourse.getYear())
                .build();
    }

    @Override
    public CpLinkResponseDto findCpLink(String courseId, int type, String professor) {

        LOGGER.info("========== LOGGING findCpLink ==========");

        CpLinkRecord cpLinkRecord = cpLinkRepo.findCpLink(courseId, type, professor);

        LOGGER.info("========== SUCCESSFUL LOGGING findSpLink ==========");
        return mappers.cpLinkRecordToCpLinkResponseDto(cpLinkRecord);
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
    public PostResponseDto addPost(PostRequestDto post) {

        LOGGER.info("========== LOGGING addPost ==========");

        PostRecord postRecord = postRepo.addPost(mappers.postRequestDtoToPost(post));
        PostResponseDto postResponseDto = mappers.postRecordToPostResponseDto(postRecord);
        jmsTemplate.convertAndSend(notificationQueue, objectMapper.writeValueAsString(postResponseDto));

        LOGGER.info("========== SUCCESSFUL LOGGING addPost ==========");
        return postResponseDto;
    }

    @Override
    public PostsResponseDto findPostsByCourseId(String courseId) {

        LOGGER.info("========== LOGGING findPostsByCourseId ==========");

        List<PostRecord> postRecords = postRepo.findPostsByCourseId(courseId);

        LOGGER.info("========== SUCCESSFUL LOGGING findPostsByCourseId ==========");
        return PostsResponseDto.builder()
                .data(mappers.postsRecordsToPostsResponseDto(postRecords))
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

}
