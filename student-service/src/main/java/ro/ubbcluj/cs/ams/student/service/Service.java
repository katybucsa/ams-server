package ro.ubbcluj.cs.ams.student.service;

import ro.ubbcluj.cs.ams.student.dto.CoursesIdsDto;
import ro.ubbcluj.cs.ams.student.dto.SGroups;
import ro.ubbcluj.cs.ams.student.dto.StudentsDto;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.SGroup;

import java.util.List;

public interface Service {

    CoursesIdsDto findAllStudentEnrollments(String studentId);

    SGroups findAllGroupsBySpecId(Integer specId);

    StudentsDto findAllStudentsByCourseAndGroupId(String courseId, Integer groupId);

    SGroup findGroupById(Integer groupId);

    List<String> findAllCourseEnrollments(String courseId);
}
