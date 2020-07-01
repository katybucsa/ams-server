package ro.ubbcluj.cs.ams.course.repository.specializationRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.course.model.tables.records.SpecializationRecord;
import ro.ubbcluj.cs.ams.course.repository.specializationRepo.SpecializationRepo;

import static ro.ubbcluj.cs.ams.course.model.tables.Specialization.SPECIALIZATION;


@Component
public class SpecializationRepoImpl implements SpecializationRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(SpecializationRepoImpl.class);

    @Override
    public SpecializationRecord findById(Integer id) {

        LOGGER.info("========== LOGGING findById: id {} ==========", id);

        SpecializationRecord specializationRecord = dsl.selectFrom(SPECIALIZATION)
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING findById: id {} ==========", id);
        return specializationRecord;
    }
}
