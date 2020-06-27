package ro.ubbcluj.cs.ams.student.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ro.ubbcluj.cs.ams.student.dto.CoursesIdsDto;
import ro.ubbcluj.cs.ams.student.dto.Mappers;
import ro.ubbcluj.cs.ams.student.dto.SGroups;
import ro.ubbcluj.cs.ams.student.dto.StudentsDto;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.SGroup;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.Student;
import ro.ubbcluj.cs.ams.student.model.tables.records.EnrollmentRecord;
import ro.ubbcluj.cs.ams.student.repository.enrollmentRepo.EnrollmentRepo;
import ro.ubbcluj.cs.ams.student.repository.groupRepo.GroupRepo;
import ro.ubbcluj.cs.ams.student.repository.studentRepo.StudentRepo;
import ro.ubbcluj.cs.ams.student.service.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Autowired
    private EnrollmentRepo enrollmentRepo;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private Mappers mappers;

    private final Logger LOGGER = LoggerFactory.getLogger(ServiceImpl.class);

    @Override
    public CoursesIdsDto findAllStudentEnrollments(String studentId) {

        LOGGER.info("========== LOGGING findAllStudentEnrollments ==========");
        List<String> coursesIds = enrollmentRepo.findAllStudentEnrollments(studentId)
                .stream()
                .map(EnrollmentRecord::getCourseId)
                .collect(Collectors.toList());

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllStudentEnrollments ==========");
        return CoursesIdsDto.builder()
                .data(coursesIds)
                .build();
    }

    @Override
    public SGroups findAllGroupsBySpecId(Integer specId) {

        LOGGER.info("========== LOGGING findAllGroupsBySpecId ==========");

        List<SGroup> sGroups = mappers.groupRecordsToGroups(groupRepo.findAllGroupsBySpecId(specId));

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllGroupsBySpecId ==========");
        return SGroups.builder()
                .data(sGroups)
                .build();
    }

    @Override
    public StudentsDto findAllStudentsByCourseAndGroupId(String courseId, Integer groupId) {

        LOGGER.info("========== LOGGING findAllStudentsByGroupId ==========");

        List<EnrollmentRecord> enrolledStudents = enrollmentRepo.findAllEnrolledStudents(courseId);

        if (enrolledStudents.size() == 0) {
            return StudentsDto.builder()
                    .data(Collections.emptyList())
                    .build();
        }

        List<Student> students = mappers.studentsRecordToStudents(studentRepo.findAllStudentsByGroupId(groupId, enrolledStudents));

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllStudentsByGroupId ==========");
        return StudentsDto.builder()
                .data(students).build();
    }

    @Override
    public SGroup findGroupById(Integer groupId) {

        LOGGER.info("========== LOGGING findGroupById ==========");
        SGroup sGroup = mappers.groupRecordToGroup(groupRepo.findById(groupId));

        LOGGER.info("========== SUCCESSFULLY LOGGING findGroupById ==========");
        return sGroup;
    }

    @Override
    public List<String> findAllCourseEnrollments(String courseId) {

        LOGGER.info("========== LOGGING findAllCourseEnrollments ==========");

        List<String> courseEnrollments = enrollmentRepo.findAllEnrolledStudents(courseId)
                .stream()
                .map(EnrollmentRecord::getStudentId)
                .collect(Collectors.toList());

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllCourseEnrollments ==========");
        return courseEnrollments;
    }
}
