package ro.ubbcluj.cs.ams.subject.dao.subjectCodeDao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.subject.dao.subjectCodeDao.SubjectCodeDao;
import ro.ubbcluj.cs.ams.subject.dao.subjectDao.impl.SubjectDaoImpl;
import ro.ubbcluj.cs.ams.subject.model.Tables;
import ro.ubbcluj.cs.ams.subject.model.tables.pojos.SubjectCode;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SubjectCodeRecord;

@Component
public class SubjectCodeDaoImpl implements SubjectCodeDao {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(SubjectDaoImpl.class);

    @Override
    public SubjectCodeRecord findBySubjectName(String name) {

        logger.info("++++++++ LOGGING findBySubjectName ++++++++");

        SubjectCodeRecord subjectCodeRecord = dsl.selectFrom(Tables.SUBJECT_CODE)
                .where(Tables.SUBJECT_CODE.SUBJECT_NAME.eq(name))
                .fetchOne();
        return subjectCodeRecord;
    }

    @Override
    public Integer addSubjectCode(SubjectCode subjectCode) {

        logger.info("++++++++++ LOGGING addSubjectCode ++++++++++");

        Record1<Integer> code = dsl.insertInto(Tables.SUBJECT_CODE, Tables.SUBJECT_CODE.SUBJECT_NAME)
                .values(subjectCode.getSubjectName())
                .returningResult(Tables.SUBJECT_CODE.CODE)
                .fetchOne();

        return code.value1();
    }
}
