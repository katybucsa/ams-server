package ro.ubbcluj.cs.ams.course.repository.courseCodeRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.CourseCode;
import ro.ubbcluj.cs.ams.course.model.tables.records.CourseCodeRecord;
import ro.ubbcluj.cs.ams.course.repository.courseCodeRepo.CourseCodeRepo;

import static ro.ubbcluj.cs.ams.course.model.tables.CourseCode.COURSE_CODE;

@Component
public class CourseCodeRepoImpl implements CourseCodeRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(CourseCodeRepoImpl.class);

    @Override
    public CourseCodeRecord findByCourseName(String name) {

        LOGGER.info("========== LOGGING findBySubjectName ==========");

        CourseCodeRecord courseCodeRecord = dsl.selectFrom(COURSE_CODE)
                .where(COURSE_CODE.COURSE_NAME.eq(name))
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING findByCourseName ==========");
        return courseCodeRecord;
    }

    @Override
    public Integer addCourseCode(CourseCode courseCode) {

        LOGGER.info("========== LOGGING addCourseCode ==========");

        Record1<Integer> code = dsl.insertInto(COURSE_CODE, COURSE_CODE.COURSE_NAME)
                .values(courseCode.getCourseName())
                .returningResult(COURSE_CODE.CODE)
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING addCourseCode ==========");
        return code.value1();
    }
}
