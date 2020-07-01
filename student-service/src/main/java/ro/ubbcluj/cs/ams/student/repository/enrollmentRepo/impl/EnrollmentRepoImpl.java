package ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.student.model.Tables;
import ro.ubbcluj.cs.ams.student.model.tables.records.EnrollmentRecord;
import ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.EnrollmentRepo;

import java.util.List;

import static ro.ubbcluj.cs.ams.student.model.tables.Enrollment.ENROLLMENT;

@Repository
public class EnrollmentRepoImpl implements EnrollmentRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollmentRepoImpl.class);

    @Override
    public List<EnrollmentRecord> findAllStudentEnrollments(String studentId) {

        LOGGER.info("========== LOGGING findAllStudentEnrollments ==========");
        LOGGER.info("Student id {}", studentId);

        List<EnrollmentRecord> enrollmentRecordList = dsl.selectFrom(Tables.ENROLLMENT)
                .where(Tables.ENROLLMENT.STUDENT_ID.eq(studentId))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllStudentEnrollments ========== ");
        return enrollmentRecordList;
    }

    @Override
    public List<EnrollmentRecord> findAllEnrolledStudents(String courseId) {

        LOGGER.info("========== LOGGING findAllEnrolledStudents ==========");
        LOGGER.info("Course id {}", courseId);

        List<EnrollmentRecord> enrollmentRecords = dsl.selectFrom(ENROLLMENT)
                .where(ENROLLMENT.COURSE_ID.eq(courseId))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllEnrolledStudents ========== ");
        return enrollmentRecords;
    }
}
