package ro.ubbcluj.cs.ams.course.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.course.model.tables.records.CourseRecord;
import ro.ubbcluj.cs.ams.course.repository.courseRepo.CourseRepo;

import java.util.List;

@Component
public class CourseQueries implements GraphQLQueryResolver {

    @Autowired
    private CourseRepo courseRepo;

    private final Logger LOGGER = LoggerFactory.getLogger(CourseQueries.class);

    public List<CourseRecord> courses(List<String> coursesIds) {

        LOGGER.info("========== LOGGING findAllCoursesByIds ==========");

        List<CourseRecord> courseRecords = courseRepo.findAllCoursesByIds(coursesIds);

        LOGGER.info("========== SUCCESSFUL LOGGING findAllCoursesByIds ==========");
        return courseRecords;
    }
}
