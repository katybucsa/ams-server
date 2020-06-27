package ro.ubbcluj.cs.ams.assignment.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import ro.ubbcluj.cs.ams.assignment.dto.GradeDto;
import ro.ubbcluj.cs.ams.assignment.dto.Mappers;
import ro.ubbcluj.cs.ams.assignment.dto.GradeResponseDto;
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

    private final Logger logger = LogManager.getLogger(ServiceImpl.class);

    @Override
    public GradeResponseDto addGrade(GradeDto gradeDto, String teacher) {

        logger.info("========== LOGGING addGrade ==========");

        Grade grade = mappers.gradeDtoToGrade(gradeDto);
        grade.setTeacher(teacher);
        grade.setDate(LocalDateTime.ofInstant(provider.get(), ZoneId.systemDefault()));
        GradeRecord gradeRecord = assignmentRepo.addGrade(grade);

        logger.info("========== SUCCESSFUL LOGGING addGrade ==========");
        return mappers.gradeRecordToGradeResponseDto(gradeRecord);
    }

    @Override
    @Cacheable(value = "studentGrades", key = "{#student, #courseId}")
    public List<Grade> findAllGradesByStudentAndCourseId(String student, String courseId) {

        logger.info("========== LOGGING findAllGradesByStudentAndCourseId ==========");

        List<Grade> gradeRecordList = mappers.gradeRecordToGrade(assignmentRepo.getAllGradesByStudentAndCourseId(student, courseId));

        logger.info("========== SUCCESSFULLY LOGGING findAllGradesByStudentAndCourseId ==========");
        return gradeRecordList;
    }
}
