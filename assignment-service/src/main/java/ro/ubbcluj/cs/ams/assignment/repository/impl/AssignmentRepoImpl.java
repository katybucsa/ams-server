package ro.ubbcluj.cs.ams.assignment.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.model.tables.records.GradeRecord;
import ro.ubbcluj.cs.ams.assignment.repository.AssignmentRepo;

import java.util.List;

import static ro.ubbcluj.cs.ams.assignment.model.tables.Grade.GRADE;

@Repository
public class AssignmentRepoImpl implements AssignmentRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(AssignmentRepoImpl.class);

    @Override
    public GradeRecord addGrade(Grade grade) {

        LOGGER.info("========== LOGGING addGrade ==========");
        LOGGER.info("Grade {}", grade);

        GradeRecord gradeRecord = dsl.insertInto(GRADE, GRADE.TYPE_ID, GRADE.TEACHER, GRADE.STUDENT, GRADE.VALUE, GRADE.COURSE_ID, GRADE.DATE)
                .values(grade.getTypeId(), grade.getTeacher(), grade.getStudent(), grade.getValue(), grade.getCourseId(), grade.getDate())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY addGrade ==========");
        return gradeRecord;
    }

    @Override
    public List<GradeRecord> getAllGradesByStudentAndCourseId(String studentUsername, String courseId) {

        LOGGER.info("========== LOGGING getAllGradesByStudentAndCourseId ==========");
        LOGGER.info("Student username: {}, course id: {}", studentUsername, courseId);

        List<GradeRecord> gradeRecordList = dsl.selectFrom(GRADE)
                .where(GRADE.STUDENT.eq(studentUsername))
                .and(GRADE.COURSE_ID.eq(courseId))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING getAllGradesByStudentAndCourseId ==========");
        return gradeRecordList;
    }
}
