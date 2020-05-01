package ro.ubbcluj.cs.ams.attendance.dao.attendanceInfoDao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.attendance.dao.attendanceInfoDao.AttendanceInfoDao;
import ro.ubbcluj.cs.ams.attendance.model.Tables;
import ro.ubbcluj.cs.ams.attendance.model.tables.pojos.AttendanceInfo;
import ro.ubbcluj.cs.ams.attendance.model.tables.records.AttendanceInfoRecord;


@Component
public class AttendanceInfoDaoImpl implements AttendanceInfoDao {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(AttendanceInfoDaoImpl.class);

    @Override
    public Integer addAttendanceInfo(AttendanceInfo attendanceInfo) {

        logger.info("++++++++ Before insert action for attendance info " + attendanceInfo.toString() + " +++++++++++");

        Record1<Integer> attendance_info_id = dsl.insertInto(Tables.ATTENDANCE_INFO, Tables.ATTENDANCE_INFO.COURSE_ID, Tables.ATTENDANCE_INFO.ACTIVITY_ID, Tables.ATTENDANCE_INFO.PROFESSOR_ID, Tables.ATTENDANCE_INFO.CREATED_AT, Tables.ATTENDANCE_INFO.REMAINING_TIME)
                .values(attendanceInfo.getCourseId(), attendanceInfo.getActivityId(), attendanceInfo.getProfessorId(), attendanceInfo.getCreatedAt(), attendanceInfo.getRemainingTime())
                .returningResult(Tables.ATTENDANCE_INFO.ID)
                .fetchOne();

        logger.info("+++++++ Id of attendance_info object is : " + attendance_info_id + " ++++++++");

        if (attendance_info_id == null)
            return null;
        return attendance_info_id.value1();
    }

    @Override
    public AttendanceInfoRecord findById(Integer id) {

        logger.info("++++++++ Before search for attendance info by id : " + id + " +++++++++++");

        AttendanceInfoRecord attendanceInfoRecord = dsl.selectFrom(Tables.ATTENDANCE_INFO)
                .where(Tables.ATTENDANCE_INFO.ID.eq(id))
                .fetchOne();
        return attendanceInfoRecord;
    }
}
