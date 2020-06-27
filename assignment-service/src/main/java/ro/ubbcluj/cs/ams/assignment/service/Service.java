package ro.ubbcluj.cs.ams.assignment.service;

import ro.ubbcluj.cs.ams.assignment.dto.GradeDto;
import ro.ubbcluj.cs.ams.assignment.dto.GradeResponseDto;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;

import java.util.List;

public interface Service {

    GradeResponseDto addGrade(GradeDto gradeDto, String teacher);

    List<Grade> findAllGradesByStudentAndCourseId(String student, String subjectId);
}
