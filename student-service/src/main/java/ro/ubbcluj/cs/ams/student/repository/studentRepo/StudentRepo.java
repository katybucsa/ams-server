package ro.ubbcluj.cs.ams.student.repository.studentRepo;

import ro.ubbcluj.cs.ams.student.model.tables.records.EnrollmentRecord;
import ro.ubbcluj.cs.ams.student.model.tables.records.StudentRecord;

import java.util.List;

public interface StudentRepo {

    List<StudentRecord> findAllStudentsByGroupId(Integer groupId, List<EnrollmentRecord> enrollments);
}
