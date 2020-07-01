package ro.ubbcluj.cs.ams.assignment.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import ro.ubbcluj.cs.ams.assignment.dto.grade.GradeDto;
import ro.ubbcluj.cs.ams.assignment.service.Mappers;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.model.tables.records.GradeRecord;
import ro.ubbcluj.cs.ams.assignment.repository.AssignmentRepo;
import ro.ubbcluj.cs.ams.assignment.service.Service;

import javax.inject.Provider;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Autowired
    private Mappers mappers;

    @Autowired
    private Provider<Instant> provider;

    @Autowired
    private AssignmentRepo assignmentRepo;

    private static final Logger LOGGER = LogManager.getLogger(ServiceImpl.class);

    @Override
    public Grade addGrade(GradeDto gradeDto, String teacher) {

        LOGGER.info("========== LOGGING addGrade ==========");

        Grade grade = mappers.gradeDtoToGrade(gradeDto);
        grade.setTeacher(teacher);
        grade.setDate(LocalDateTime.ofInstant(provider.get(), ZoneId.systemDefault()));
        GradeRecord gradeRecord = assignmentRepo.addGrade(grade);

        LOGGER.info("========== SUCCESSFULLY LOGGING addGrade ==========");
        return mappers.gradeRecordToGrade(gradeRecord);
    }

    @Override
    @Cacheable(value = "studentGrades", key = "{#student, #courseId}")
    public List<Grade> findAllGradesByStudentAndCourseId(String student, String courseId) {

        LOGGER.info("========== LOGGING findAllGradesByStudentAndCourseId ==========");

        List<Grade> gradeRecordList = mappers.gradeRecordsToGrades(assignmentRepo.getAllGradesByStudentAndCourseId(student, courseId));

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllGradesByStudentAndCourseId ==========");
        return gradeRecordList;
    }
}
