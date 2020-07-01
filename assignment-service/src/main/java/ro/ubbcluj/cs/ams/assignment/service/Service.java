package ro.ubbcluj.cs.ams.assignment.service;

import ro.ubbcluj.cs.ams.assignment.dto.grade.GradeDto;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;

import java.util.List;

public interface Service {

    Grade addGrade(GradeDto gradeDto, String teacher);

    List<Grade> findAllGradesByStudentAndCourseId(String student, String subjectId);
}
