package ro.ubbcluj.cs.ams.assignment.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.assignment.model.Tables;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.model.tables.records.GradeRecord;
import ro.ubbcluj.cs.ams.assignment.repository.AssignmentRepo;

import java.util.List;

@Repository
public class AssignmentRepoImpl implements AssignmentRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(AssignmentRepoImpl.class);

    @Override
    public GradeRecord addGrade(Grade grade) {

        logger.info("========== Before addGrade ==========");
        logger.info("Grade {}", grade);

        GradeRecord gradeRecord = dsl.insertInto(Tables.GRADE, Tables.GRADE.TYPE_ID, Tables.GRADE.TEACHER, Tables.GRADE.STUDENT, Tables.GRADE.VALUE, Tables.GRADE.SUBJECT_ID, Tables.GRADE.DATE)
                .values(grade.getTypeId(), grade.getTeacher(), grade.getStudent(), grade.getValue(), grade.getSubjectId(), grade.getDate())
                .returning()
                .fetchOne();
        logger.info("========== addGrade successful ==========");
        return gradeRecord;
    }

    @Override
    public List<GradeRecord> getAllGradesByStudentAndSubjectId(String studentUsername, String subjectId) {

        logger.info("========== LOGGING getAllGradesByStudentAndSubjectId ==========");
        logger.info("Student username: {}, subject id: {}", studentUsername, subjectId);

        List<GradeRecord> gradeRecordList = dsl.selectFrom(Tables.GRADE)
                .where(Tables.GRADE.STUDENT.eq(studentUsername))
                .and(Tables.GRADE.SUBJECT_ID.eq(subjectId))
                .fetch();

        logger.info("========== SUCCESSFUL LOGGING getAllGradesByStudentAndSubjectId ==========");
        return gradeRecordList;
    }
}
