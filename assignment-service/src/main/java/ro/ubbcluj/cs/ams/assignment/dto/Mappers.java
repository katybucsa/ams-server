package ro.ubbcluj.cs.ams.assignment.dto;

import org.mapstruct.Mapper;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.model.tables.records.GradeRecord;

import java.util.List;


@Mapper(componentModel = "spring")
public interface Mappers {

    Grade gradeDtoToGrade(GradeDto gradeDto);

    GradeResponseDto gradeRecordToGradeResponseDto(GradeRecord gradeRecord);

    List<GradeResponseDto> gradeRecordsToGradeResponseDtos(List<GradeRecord> gradeRecordList);

    List<Grade> gradeRecordToGrade(List<GradeRecord> allGradesByStudentAndCourseId);
}
