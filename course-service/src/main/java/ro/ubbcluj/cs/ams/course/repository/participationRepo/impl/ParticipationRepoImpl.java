package ro.ubbcluj.cs.ams.course.repository.participationRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Participation;
import ro.ubbcluj.cs.ams.course.model.tables.records.ParticipationRecord;
import ro.ubbcluj.cs.ams.course.repository.participationRepo.ParticipationRepo;

import java.util.List;

import static ro.ubbcluj.cs.ams.course.model.tables.Participation.PARTICIPATION;

@Repository
public class ParticipationRepoImpl implements ParticipationRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(ParticipationRepoImpl.class);

    @Override
    public int addOrDeleteParticipation(Participation participation) {

        LOGGER.info("========== LOGGING addOrDeleteParticipation ==========");

        int inserted = 0;
        int deleted = dsl.deleteFrom(PARTICIPATION)
                .where(PARTICIPATION.USER_ID.eq(participation.getUserId())
                        .and(PARTICIPATION.EVENT_ID.eq(participation.getEventId())))
                .execute();

        if (deleted == 0) {

            LOGGER.info("========== add participation ==========");
            inserted = dsl.insertInto(PARTICIPATION, PARTICIPATION.EVENT_ID, PARTICIPATION.USER_ID)
                    .values(participation.getEventId(), participation.getUserId())
                    .execute();
        } else {
            LOGGER.info("========== delete participation ==========");
        }

        LOGGER.info("========== SUCCESSFULLY LOGGING addOrDeleteParticipation ==========");
        return inserted;
    }

    @Override
    public List<ParticipationRecord> findAllByUserId(String userId) {

        LOGGER.info("========== LOGGING findAllByUserId ==========");

        List<ParticipationRecord> participationRecords = dsl.selectFrom(PARTICIPATION)
                .where(PARTICIPATION.USER_ID.eq(userId))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllByUserId ==========");
        return participationRecords;
    }

    @Override
    public List<ParticipationRecord> findAllByEventId(int eventId) {

        LOGGER.info("========== LOGGING findAllByEventId ==========");

        List<ParticipationRecord> participationRecords = dsl.selectFrom(PARTICIPATION)
                .where(PARTICIPATION.EVENT_ID.eq(eventId))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllByEventId ==========");
        return participationRecords;
    }
}
