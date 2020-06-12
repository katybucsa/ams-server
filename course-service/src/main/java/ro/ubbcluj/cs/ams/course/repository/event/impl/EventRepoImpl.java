package ro.ubbcluj.cs.ams.course.repository.event.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.course.model.Tables;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Event;
import ro.ubbcluj.cs.ams.course.model.tables.records.EventRecord;
import ro.ubbcluj.cs.ams.course.repository.cpLinkRepo.impl.CpLinkRepoImpl;
import ro.ubbcluj.cs.ams.course.repository.event.EventRepo;

@Repository
public class EventRepoImpl implements EventRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(EventRepoImpl.class);

    @Override
    public EventRecord addEvent(Event event) {

        LOGGER.info("========== LOGGING addEvent ==========");

        EventRecord eventRecord = dsl.insertInto(Tables.EVENT, Tables.EVENT.ID, Tables.EVENT.DATE, Tables.EVENT.HOUR, Tables.EVENT.PLACE)
                .values(event.getId(), event.getDate(), event.getHour(), event.getPlace())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING addEvent ==========");
        return eventRecord;

    }

    @Override
    public EventRecord findById(int id) {

        LOGGER.info("========== LOGGING findById ==========");

        EventRecord eventRecord = dsl.selectFrom(Tables.EVENT)
                .where(Tables.EVENT.ID.eq(id))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findById ==========");
        return eventRecord;
    }
}
