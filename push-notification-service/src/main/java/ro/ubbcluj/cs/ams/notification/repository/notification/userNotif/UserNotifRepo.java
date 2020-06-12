package ro.ubbcluj.cs.ams.notification.repository.notification.userNotif;

import ro.ubbcluj.cs.ams.notification.model.tables.records.UserNotifRecord;

import java.util.List;

public interface UserNotifRepo {

    List<UserNotifRecord> findAllNotificationsForUser(String username);
}
