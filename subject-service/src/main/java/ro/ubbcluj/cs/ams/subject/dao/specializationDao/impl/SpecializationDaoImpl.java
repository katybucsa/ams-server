package ro.ubbcluj.cs.ams.subject.dao.specializationDao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.subject.dao.specializationDao.SpecializationDao;
import ro.ubbcluj.cs.ams.subject.model.Tables;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SpecializationRecord;


@Component
public class SpecializationDaoImpl implements SpecializationDao {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(SpecializationDaoImpl.class);

    @Override
    public SpecializationRecord findById(Integer id) {

        logger.info("++++++++++ Befor find Specialization by id :" + id + " +++++++++++++");

        SpecializationRecord specializationRecord = dsl.selectFrom(Tables.SPECIALIZATION)
                .fetchOne();
        return specializationRecord;
    }
}
