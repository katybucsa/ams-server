package ro.ubbcluj.cs.ams.attendance.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import ro.ubbcluj.cs.ams.attendance.dao.attendanceDao.AttendanceDao;
import ro.ubbcluj.cs.ams.attendance.dao.attendanceInfoDao.AttendanceInfoDao;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceInfoReq;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceInfoResponse;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceRequest;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceResponse;
import ro.ubbcluj.cs.ams.attendance.model.tables.pojos.Attendance;
import ro.ubbcluj.cs.ams.attendance.model.tables.pojos.AttendanceInfo;
import ro.ubbcluj.cs.ams.attendance.model.tables.records.AttendanceInfoRecord;
import ro.ubbcluj.cs.ams.attendance.model.tables.records.AttendanceRecord;
import ro.ubbcluj.cs.ams.attendance.service.Service;
import ro.ubbcluj.cs.ams.attendance.service.exception.AttendanceExceptionType;
import ro.ubbcluj.cs.ams.attendance.service.exception.AttendanceServiceException;

import javax.inject.Provider;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@org.springframework.stereotype.Service
public class AttendanceService implements Service {

    @Autowired
    private AttendanceInfoDao attendanceInfoDao;

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private Provider<Instant> instantProvider;

    @Value("${separator.barcode}")
    private String separator;

    private Logger logger = LogManager.getLogger(AttendanceService.class);

    public AttendanceInfoResponse addAttendanceInfo(AttendanceInfoReq attendanceInfoReq, String username) {

        logger.info("+++++++++ addAttendanceInfo in service +++++++");

        LocalDateTime createdAt = LocalDateTime.ofInstant(instantProvider.get(), ZoneId.systemDefault());
        AttendanceInfo attendanceInfo = new AttendanceInfo(Integer.parseInt("0"), attendanceInfoReq.getCourseId(), attendanceInfoReq.getActivityId(), username, createdAt, attendanceInfoReq.getRemainingTime());
        Integer attendanceInfo_id = attendanceInfoDao.addAttendanceInfo(attendanceInfo);

        String barcode = this.generateBarcode(attendanceInfo_id);

        AttendanceInfoResponse attendanceInfoResponse = AttendanceInfoResponse.builder()
                .barcode(barcode)
                .createdAt(createdAt)
                .build();
        return attendanceInfoResponse;
    }

    @Override
    public AttendanceResponse addAttendance(AttendanceRequest attendanceRequest, String username) {

        logger.info("+++++++++ addAttendance in service +++++++");

        AttendanceInfoRecord attendanceInfoRecord = attendanceInfoDao.findById(attendanceRequest.getAttendanceInfoId());
        if (attendanceInfoRecord == null)
            throw new AttendanceServiceException("Attendance info not found", AttendanceExceptionType.ATTENDANCE_INFO_NOT_FOUND, HttpStatus.BAD_REQUEST);

        LocalDateTime currentTime = LocalDateTime.ofInstant(instantProvider.get(), ZoneOffset.UTC);
        LocalDateTime createdAt = attendanceInfoRecord.getCreatedAt();

        if (createdAt.plusMinutes(attendanceInfoRecord.getRemainingTime()).isBefore(currentTime))
            throw new AttendanceServiceException("Time for this attendance has expired! ", AttendanceExceptionType.INVALID_TIME, HttpStatus.BAD_REQUEST);

        Attendance attendance = new Attendance(Integer.valueOf("0"), username, createdAt, attendanceRequest.getAttendanceInfoId());
        AttendanceRecord attendanceAdded = attendanceDao.addAttendance(attendance);


        AttendanceResponse attendanceResponse = AttendanceResponse.builder()
                .course(attendanceInfoRecord.getCourseId())
                .activity(attendanceInfoRecord.getActivityId())
                .build();
        return attendanceResponse;
    }

    private String generateBarcode(Integer id) {

        String id_separator = id + separator;
        String random_string = RandomStringUtils.randomAlphabetic(100);
        return id_separator + random_string;
    }
}
