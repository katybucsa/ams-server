package ro.ubbcluj.cs.ams.course.repository.activityTypes.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.course.model.tables.records.ActivityTypeRecord;
import ro.ubbcluj.cs.ams.course.repository.activityTypes.ActivityTypeRepo;
import ro.ubbcluj.cs.ams.course.repository.event.impl.EventRepoImpl;

import java.util.List;

import static ro.ubbcluj.cs.ams.course.model.tables.ActivityType.ACTIVITY_TYPE;

@Repository
public class ActivityTypeRepoImpl implements ActivityTypeRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(ActivityTypeRepoImpl.class);


    @Override
    public List<ActivityTypeRecord> findActivityTypes() {

        LOGGER.info("========== LOGGING findActivityTypes ==========");

        List<ActivityTypeRecord> list = dsl.selectFrom(ACTIVITY_TYPE)
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findActivityTypes ==========");
        return list;
    }

    @Override
    public ActivityTypeRecord findActivityTypeById(int typeId) {

        LOGGER.info("========== LOGGING findActivityTypeById ==========");

        ActivityTypeRecord activityTypeRecord=dsl.selectFrom(ACTIVITY_TYPE)
                .where(ACTIVITY_TYPE.TYPE_ID.eq(typeId))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findActivityTypeById ==========");
        return activityTypeRecord;
    }
}
