package ro.ubbcluj.cs.ams.student.repository.enrollmentRepo;

import ro.ubbcluj.cs.ams.student.model.tables.records.EnrollmentRecord;

import java.util.List;

public interface EnrollmentRepo {

    List<EnrollmentRecord> findAllStudentEnrollments(String studentId);
}
