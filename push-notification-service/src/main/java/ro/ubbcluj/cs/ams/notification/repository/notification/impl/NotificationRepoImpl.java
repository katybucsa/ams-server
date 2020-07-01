package ro.ubbcluj.cs.ams.notification.repository.notification.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.records.NotificationRecord;
import ro.ubbcluj.cs.ams.notification.repository.notification.NotificationRepo;

import static ro.ubbcluj.cs.ams.notification.model.tables.Notification.NOTIFICATION;

@Repository
public class NotificationRepoImpl implements NotificationRepo {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationRepoImpl.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public NotificationRecord addNotification(Notification notification) {

        LOGGER.info("========== LOGGING addNotification ==========");

        NotificationRecord notificationRecord = dsl.insertInto(NOTIFICATION, NOTIFICATION.POST_ID, NOTIFICATION.TYPE, NOTIFICATION.COURSE_ID, NOTIFICATION.TITLE, NOTIFICATION.BODY, NOTIFICATION.DATE)
                .values(notification.getPostId(), notification.getType(), notification.getCourseId(), notification.getTitle(), notification.getBody(),notification.getDate())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING addNotification ==========");
        return notificationRecord;
    }

    @Override
    public NotificationRecord findNotification(Integer postId, String type) {

        LOGGER.info("========== LOGGING findNotification ==========");

        NotificationRecord notificationRecord = dsl.selectFrom(NOTIFICATION)
                .where(NOTIFICATION.POST_ID.eq(postId).and(NOTIFICATION.TYPE.eq(type)))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findNotification ==========");
        return notificationRecord;
    }

    @Override
    public NotificationRecord updateNotification(Notification notification) {

        LOGGER.info("========== LOGGING updateNotification ==========");

        NotificationRecord notificationRecord = dsl.update(NOTIFICATION)
                .set(NOTIFICATION.BODY, notification.getBody())
                .set(NOTIFICATION.DATE,notification.getDate())
                .where(NOTIFICATION.ID.eq(notification.getId()))
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING updateNotification ==========");
        return notificationRecord;
    }

    @Override
    public NotificationRecord findNotification(Integer notifId) {

        LOGGER.info("========== LOGGING findNotification ==========");

        NotificationRecord notificationRecord = dsl.selectFrom(NOTIFICATION)
                .where(NOTIFICATION.ID.eq(notifId))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findNotification ==========");
        return notificationRecord;
    }
}
