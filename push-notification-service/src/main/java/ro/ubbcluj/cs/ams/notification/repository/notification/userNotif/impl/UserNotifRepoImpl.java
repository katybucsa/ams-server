package ro.ubbcluj.cs.ams.notification.repository.notification.userNotif.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.notification.model.tables.records.UserNotifRecord;
import ro.ubbcluj.cs.ams.notification.repository.notification.userNotif.UserNotifRepo;
import ro.ubbcluj.cs.ams.notification.repository.subscription.impl.SubscriptionRepoImpl;

import java.util.List;

@Repository
public class UserNotifRepoImpl implements UserNotifRepo {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserNotifRepoImpl.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public List<UserNotifRecord> findAllNotificationsForUser(String username) {
        return null;
    }
}
