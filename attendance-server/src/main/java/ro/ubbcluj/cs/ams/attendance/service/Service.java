package ro.ubbcluj.cs.ams.attendance.service;

import ro.ubbcluj.cs.ams.attendance.dto.AttendanceInfoReq;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceInfoResponse;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceRequest;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceResponse;

public interface Service {

    AttendanceInfoResponse addAttendanceInfo(AttendanceInfoReq attendanceInfoReq, String username);

    AttendanceResponse addAttendance(AttendanceRequest attendanceRequest, String username);
}
