package ro.ubbcluj.cs.ams.student.service;

import ro.ubbcluj.cs.ams.student.dto.CoursesIdsDto;

public interface Service {

    CoursesIdsDto findAllStudentEnrollments(String studentId);
}
