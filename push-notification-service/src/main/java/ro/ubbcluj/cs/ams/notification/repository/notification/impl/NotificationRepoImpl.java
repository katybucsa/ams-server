package ro.ubbcluj.cs.ams.notification.repository.notification.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.notification.model.Tables;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.records.NotificationRecord;
import ro.ubbcluj.cs.ams.notification.repository.notification.NotificationRepo;

@Repository
public class NotificationRepoImpl implements NotificationRepo {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationRepoImpl.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public NotificationRecord addNotification(Notification notification) {

        LOGGER.info("========== LOGGING addNotification ==========");

        NotificationRecord notificationRecord = dsl.insertInto(Tables.NOTIFICATION, Tables.NOTIFICATION.POST_ID, Tables.NOTIFICATION.TITLE, Tables.NOTIFICATION.BODY)
                .values(notification.getPostId(), notification.getTitle(), notification.getBody())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING addNotification ==========");
        return notificationRecord;
    }
}
