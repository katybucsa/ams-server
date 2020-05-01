package ro.ubbcluj.cs.ams.attendance.dao.attendanceDao;

import ro.ubbcluj.cs.ams.attendance.model.tables.pojos.Attendance;
import ro.ubbcluj.cs.ams.attendance.model.tables.records.AttendanceRecord;

public interface AttendanceDao {

    AttendanceRecord addAttendance(Attendance attendance);
}
