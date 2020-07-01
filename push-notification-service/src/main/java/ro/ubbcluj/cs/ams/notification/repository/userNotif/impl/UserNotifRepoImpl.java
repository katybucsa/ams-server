package ro.ubbcluj.cs.ams.notification.repository.userNotif.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.UserNotif;
import ro.ubbcluj.cs.ams.notification.model.tables.records.UserNotifRecord;
import ro.ubbcluj.cs.ams.notification.repository.userNotif.UserNotifRepo;

import java.util.List;
import java.util.Objects;

import static ro.ubbcluj.cs.ams.notification.model.tables.UserNotif.USER_NOTIF;


@Repository
public class UserNotifRepoImpl implements UserNotifRepo {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserNotifRepoImpl.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public List<UserNotifRecord> findAllNotificationsForUser(String username) {

        LOGGER.info("========== LOGGING findAllNotificationsForUser ==========");

        List<UserNotifRecord> userNotifRecords = dsl.selectFrom(USER_NOTIF)
                .where(USER_NOTIF.USER_ID.eq(username))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllNotificationsForUser ==========");
        return userNotifRecords;
    }

    @Override
    public void addUserNotification(UserNotif userNotif) {

        LOGGER.info("========== LOGGING addUserNotification ==========");

        dsl.insertInto(USER_NOTIF, USER_NOTIF.USER_ID, USER_NOTIF.NOTIF_ID, USER_NOTIF.POST_ID, USER_NOTIF.USER_ROLE, USER_NOTIF.READ, USER_NOTIF.SEEN)
                .values(userNotif.getUserId(), userNotif.getNotifId(), userNotif.getPostId(), userNotif.getUserRole(), userNotif.getRead(), userNotif.getSeen())
                .execute();
        LOGGER.info("========== SUCCESSFULLY LOGGING addUserNotification ==========");
    }

    @Override
    public UserNotifRecord updateUserNotif(UserNotif userNotif) {

        LOGGER.info("========== LOGGING updateUserNotif ==========");

        UserNotifRecord userNotifRecord = null;
        if (!(Objects.isNull(userNotif.getRead()) && Objects.isNull(userNotif.getSeen())))
            userNotifRecord = dsl.update(USER_NOTIF)
                    .set(USER_NOTIF.READ, userNotif.getRead())
                    .set(USER_NOTIF.SEEN, userNotif.getSeen())
                    .where(USER_NOTIF.USER_ID.eq(userNotif.getUserId()).and(USER_NOTIF.NOTIF_ID.eq(userNotif.getNotifId())))
                    .returning()
                    .fetchOne();
        else if (Objects.isNull(userNotif.getRead()))
            userNotifRecord = dsl.update(USER_NOTIF)
                    .set(USER_NOTIF.SEEN, userNotif.getSeen())
                    .where(USER_NOTIF.USER_ID.eq(userNotif.getUserId()).and(USER_NOTIF.NOTIF_ID.eq(userNotif.getNotifId())))
                    .returning()
                    .fetchOne();
        else if (Objects.isNull(userNotif.getSeen()))
            userNotifRecord = dsl.update(USER_NOTIF)
                    .set(USER_NOTIF.READ, userNotif.getRead())
                    .where(USER_NOTIF.USER_ID.eq(userNotif.getUserId()).and(USER_NOTIF.NOTIF_ID.eq(userNotif.getNotifId())))
                    .returning()
                    .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING updateUserNotif ==========");
        return userNotifRecord;
    }

    @Override
    public UserNotifRecord findUserNotif(String username, Integer notifId) {

        LOGGER.info("========== LOGGING findUserNotif ==========");

        UserNotifRecord userNotifRecord = dsl.selectFrom(USER_NOTIF)
                .where(USER_NOTIF.USER_ID.eq(username).and(USER_NOTIF.NOTIF_ID.eq(notifId)))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findUserNotif ==========");
        return userNotifRecord;
    }

    @Override
    public void updateUserNotifs(String username) {

        LOGGER.info("========== LOGGING updateUserNotifs ==========");

        System.out.println(dsl.configuration().dialect());

        dsl.update(USER_NOTIF)
                .set(USER_NOTIF.SEEN, true)
                .where(USER_NOTIF.USER_ID.eq(username))
                .execute();

        LOGGER.info("========== SUCCESSFULLY LOGGING updateUserNotifs ==========");
    }

    @Override
    public int findNotSeenNotifs(String name) {

        LOGGER.info("========== LOGGING findNotSeenNotifs ==========");

        int nr = dsl.selectFrom(USER_NOTIF)
                .where(USER_NOTIF.USER_ID.eq(name).and(USER_NOTIF.SEEN.eq(false)))
                .execute();

        LOGGER.info("========== SUCCESSFULLY LOGGING findNotSeenNotifs ==========");
        return nr;
    }
}
