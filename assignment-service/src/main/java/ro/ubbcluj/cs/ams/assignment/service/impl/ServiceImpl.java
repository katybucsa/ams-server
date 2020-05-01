package ro.ubbcluj.cs.ams.assignment.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ro.ubbcluj.cs.ams.assignment.dto.GradeDto;
import ro.ubbcluj.cs.ams.assignment.dto.GradeMapper;
import ro.ubbcluj.cs.ams.assignment.dto.GradeResponseDto;
import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.model.tables.records.GradeRecord;
import ro.ubbcluj.cs.ams.assignment.repository.AssignmentRepo;
import ro.ubbcluj.cs.ams.assignment.service.Service;

import javax.inject.Provider;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private Provider<Instant> provider;

    @Autowired
    private AssignmentRepo assignmentRepo;

    private final Logger logger = LogManager.getLogger(ServiceImpl.class);

    @Override
    public GradeResponseDto addGrade(GradeDto gradeDto, String teacher) {

        logger.info("========== LOGGING addGrade ==========");

        Grade grade = gradeMapper.gradeDtoToGrade(gradeDto);
        grade.setTeacher(teacher);
        grade.setDate(LocalDateTime.ofInstant(provider.get(), ZoneId.systemDefault()));
        GradeRecord gradeRecord = assignmentRepo.addGrade(grade);

        logger.info("========== SUCCESSFUL LOGGING addGrade ==========");
        return gradeMapper.gradeRecordToGradeResponseDto(gradeRecord);
    }
}
