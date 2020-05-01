package ro.ubbcluj.cs.ams.attendance.dao.attendanceInfoDao;

import ro.ubbcluj.cs.ams.attendance.model.tables.pojos.AttendanceInfo;
import ro.ubbcluj.cs.ams.attendance.model.tables.records.AttendanceInfoRecord;

public interface AttendanceInfoDao {

    Integer addAttendanceInfo(AttendanceInfo attendanceInfo);

    AttendanceInfoRecord findById(Integer id);
}
