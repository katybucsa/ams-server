package ro.ubbcluj.cs.ams.attendance.dao.attendanceDao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.attendance.dao.attendanceDao.AttendanceDao;
import ro.ubbcluj.cs.ams.attendance.dao.attendanceInfoDao.impl.AttendanceInfoDaoImpl;
import ro.ubbcluj.cs.ams.attendance.model.Tables;
import ro.ubbcluj.cs.ams.attendance.model.tables.pojos.Attendance;
import ro.ubbcluj.cs.ams.attendance.model.tables.records.AttendanceRecord;


@Component
public class AttendanceDaoImpl implements AttendanceDao {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(AttendanceInfoDaoImpl.class);


    @Override
    public AttendanceRecord addAttendance(Attendance attendance) {

        logger.info("+++++++++++ Before addAttendance :" + attendance + "++++++++++++++++");

        AttendanceRecord attendanceRecord = dsl.insertInto(Tables.ATTENDANCE, Tables.ATTENDANCE.STUDENT_ID, Tables.ATTENDANCE.CREATED_AT, Tables.ATTENDANCE.ATTENDANCE_INFO_ID)
                .values(attendance.getStudentId(), attendance.getCreatedAt(), attendance.getAttendanceInfoId())
                .returning()
                .fetchOne();

        logger.info("+++++ After add action ++++++++++");
        return attendanceRecord;
    }
}
