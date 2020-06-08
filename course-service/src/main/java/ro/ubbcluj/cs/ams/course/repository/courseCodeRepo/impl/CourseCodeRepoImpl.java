package ro.ubbcluj.cs.ams.course.repository.courseCodeRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.course.model.Tables;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.CourseCode;
import ro.ubbcluj.cs.ams.course.model.tables.records.CourseCodeRecord;
import ro.ubbcluj.cs.ams.course.repository.courseCodeRepo.CourseCodeRepo;
import ro.ubbcluj.cs.ams.course.repository.courseRepo.impl.CourseRepoImpl;

@Component
public class CourseCodeRepoImpl  implements CourseCodeRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(CourseRepoImpl.class);

    @Override
    public CourseCodeRecord findByCourseName(String name) {

        logger.info("++++++++ LOGGING findBySubjectName ++++++++");

        CourseCodeRecord subjectCodeRecord = dsl.selectFrom(Tables.COURSE_CODE)
                .where(Tables.COURSE_CODE.COURSE_NAME.eq(name))
                .fetchOne();
        return subjectCodeRecord;
    }

    @Override
    public Integer addCourseCode(CourseCode courseCode) {

        logger.info("++++++++++ LOGGING addSubjectCode ++++++++++");

        Record1<Integer> code = dsl.insertInto(Tables.COURSE_CODE, Tables.COURSE_CODE.COURSE_NAME)
                .values(courseCode.getCourseName())
                .returningResult(Tables.COURSE_CODE.CODE)
                .fetchOne();

        return code.value1();
    }
}
