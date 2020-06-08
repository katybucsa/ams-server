package ro.ubbcluj.cs.ams.course.repository.specializationRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.course.model.Tables;
import ro.ubbcluj.cs.ams.course.model.tables.records.SpecializationRecord;
import ro.ubbcluj.cs.ams.course.repository.specializationRepo.SpecializationRepo;


@Component
public class SpecializationRepoImpl implements SpecializationRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(SpecializationRepoImpl.class);

    @Override
    public SpecializationRecord findById(Integer id) {

        logger.info("++++++++++ Before find Specialization by id :" + id + " +++++++++++++");

        return dsl.selectFrom(Tables.SPECIALIZATION)
                .fetchOne();
    }
}
