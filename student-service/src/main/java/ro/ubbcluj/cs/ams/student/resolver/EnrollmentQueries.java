package ro.ubbcluj.cs.ams.student.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.student.dto.student.CoursesIdsDto;
import ro.ubbcluj.cs.ams.student.model.tables.records.EnrollmentRecord;
import ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.EnrollmentRepo;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnrollmentQueries implements GraphQLQueryResolver {

    @Autowired
    private EnrollmentRepo enrollmentRepo;

    private final Logger logger = LoggerFactory.getLogger(EnrollmentQueries.class);

    public CoursesIdsDto enrollments() {

        logger.info("========== LOGGING findAllStudentEnrollments ==========");

        String studentId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<String> coursesIds = enrollmentRepo.findAllStudentEnrollments(studentId)
                .stream()
                .map(EnrollmentRecord::getCourseId)
                .collect(Collectors.toList());

        logger.info("========== SUCCESSFUL LOGGING findAllStudentEnrollments ==========");
        return CoursesIdsDto.builder()
                .data(coursesIds)
                .build();
    }
}
