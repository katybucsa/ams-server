package ro.ubbcluj.cs.ams.student.dto;

import org.mapstruct.Mapper;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.SGroup;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.Student;
import ro.ubbcluj.cs.ams.student.model.tables.records.SGroupRecord;
import ro.ubbcluj.cs.ams.student.model.tables.records.StudentRecord;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Mappers {


    List<SGroup> groupRecordsToGroups(List<SGroupRecord> allGroupsBySpecId);

    List<Student> studentsRecordToStudents(List<StudentRecord> allStudentsByGroupId);

    SGroup groupRecordToGroup(SGroupRecord byId);
}
