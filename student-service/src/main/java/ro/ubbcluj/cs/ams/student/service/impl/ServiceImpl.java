package ro.ubbcluj.cs.ams.student.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ro.ubbcluj.cs.ams.student.dto.CoursesIdsDto;
import ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.EnrollmentRepo;
import ro.ubbcluj.cs.ams.student.service.Service;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Autowired
    private EnrollmentRepo enrollmentRepo;

    private final Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

    @Override
    public CoursesIdsDto findAllStudentEnrollments(String studentId) {

        logger.info("========== LOGGING findAllStudentEnrollments ==========");
        List<String> coursesIds = enrollmentRepo.findAllStudentEnrollments(studentId)
                .stream()
                .map(enrollment -> enrollment.getCourseId())
                .collect(Collectors.toList());

        logger.info("========== SUCCESSFUL LOGGING findAllStudentEnrollments ==========");
        return CoursesIdsDto.builder()
                .data(coursesIds)
                .build();
    }
}
