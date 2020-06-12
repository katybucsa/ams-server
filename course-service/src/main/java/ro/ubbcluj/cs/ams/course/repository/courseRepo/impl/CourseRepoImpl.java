package ro.ubbcluj.cs.ams.course.repository.courseRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.course.model.Tables;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Course;
import ro.ubbcluj.cs.ams.course.model.tables.records.CourseRecord;
import ro.ubbcluj.cs.ams.course.repository.courseRepo.CourseRepo;

import java.util.List;

import static ro.ubbcluj.cs.ams.course.model.tables.Course.COURSE;

@Component
public class CourseRepoImpl implements CourseRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger LOGGER = LogManager.getLogger(CourseRepoImpl.class);

    @Override
    public CourseRecord addCourse(Course course) {

        LOGGER.info("========== LOGGING addCourse ==========");

        CourseRecord courseRecord = dsl.insertInto(Tables.COURSE, Tables.COURSE.ID, Tables.COURSE.NAME, Tables.COURSE.CREDITS, Tables.COURSE.SPEC_ID, Tables.COURSE.YEAR)
                .values(course.getId(), course.getName(), course.getCredits(), course.getSpecId(), course.getYear())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFUL LOGGING addCourse ==========");
        return courseRecord;
    }

    @Override
    public List<CourseRecord> findAllCoursesByIds(List<String> coursesIds) {

        LOGGER.info("========== LOGGING findAllCoursesByIds ==========");
        List<CourseRecord> coursesList = dsl.selectFrom(Tables.COURSE)
                .where(Tables.COURSE.ID.in(coursesIds))
                .fetch();

        LOGGER.info("========== SUCCESSFUL LOGGING findAllCoursesByIds ==========");
        return coursesList;
    }

    @Override
    public CourseRecord findById(String id) {

        LOGGER.info("========== LOGGING findById ==========");

        CourseRecord courseRecord = dsl.selectFrom(COURSE)
                .where(COURSE.ID.eq(id))
                .fetchAny();

        LOGGER.info("========== SUCCESSFUL LOGGING findById ==========");
        return courseRecord;

    }
}
