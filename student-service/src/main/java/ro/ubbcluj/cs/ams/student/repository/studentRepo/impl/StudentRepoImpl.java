package ro.ubbcluj.cs.ams.student.repository.studentRepo.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.student.model.tables.records.EnrollmentRecord;
import ro.ubbcluj.cs.ams.student.model.tables.records.StudentRecord;
import ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.impl.EnrollmentRepoImpl;
import ro.ubbcluj.cs.ams.student.repository.studentRepo.StudentRepo;

import java.util.List;
import java.util.stream.Collectors;

import static ro.ubbcluj.cs.ams.student.model.tables.Student.STUDENT;

@Repository
public class StudentRepoImpl implements StudentRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger LOGGER = LoggerFactory.getLogger(StudentRepoImpl.class);

    @Override
    public List<StudentRecord> findAllStudentsByGroupId(Integer groupId, List<EnrollmentRecord> enrollments) {

        LOGGER.info("========== LOGGING findAllStudentsByGroupId ==========");

        List<StudentRecord> studentRecords = dsl.selectFrom(STUDENT)
                .where(STUDENT.GROUP_ID.eq(groupId)
                        .and(STUDENT.USER_ID.in(enrollments.stream()
                                .map(EnrollmentRecord::getStudentId)
                                .collect(Collectors.toList()))))
                .fetch();
        LOGGER.info("========== SUCCESSFULLY LOGGING findAllStudentsByGroupId ========== ");
        return studentRecords;
    }
}
