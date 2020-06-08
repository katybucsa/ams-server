package ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.student.model.Tables;
import ro.ubbcluj.cs.ams.student.model.tables.records.EnrollmentRecord;
import ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.EnrollmentRepo;

import java.util.Arrays;
import java.util.List;

@Repository
public class EnrollmentRepoImpl implements EnrollmentRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LoggerFactory.getLogger(EnrollmentRepoImpl.class);

    @Override
    public List<EnrollmentRecord> findAllStudentEnrollments(String studentId) {

        logger.info("========== LOGGING findAllStudentEnrollments ==========");
        logger.info("Student id {}", studentId);

        List<EnrollmentRecord> enrollmentRecordList = dsl.selectFrom(Tables.ENROLLMENT)
                .where(Tables.ENROLLMENT.STUDENT_ID.eq(studentId))
                .fetch();

        logger.info("========== SUCCESSFUL LOGGING findAllStudentEnrollments ========== ");
        return enrollmentRecordList;
    }
}
