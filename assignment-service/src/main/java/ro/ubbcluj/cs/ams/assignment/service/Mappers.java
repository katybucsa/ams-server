package ro.ubbcluj.cs.ams.assignment.service;

import org.mapstruct.Mapper;
import ro.ubbcluj.cs.ams.assignment.dto.grade.GradeDto;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.model.tables.records.GradeRecord;

import java.util.List;


@Mapper(componentModel = "spring")
public interface Mappers {

    Grade gradeDtoToGrade(GradeDto gradeDto);

    List<Grade> gradeRecordsToGrades(List<GradeRecord> allGradesByStudentAndCourseId);

    Grade gradeRecordToGrade(GradeRecord gradeRecord);
}
