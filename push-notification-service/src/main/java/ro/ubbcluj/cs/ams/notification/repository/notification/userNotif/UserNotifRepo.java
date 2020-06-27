package ro.ubbcluj.cs.ams.notification.repository.notification.userNotif;

import ro.ubbcluj.cs.ams.notification.model.tables.pojos.UserNotif;
import ro.ubbcluj.cs.ams.notification.model.tables.records.UserNotifRecord;

import java.util.List;

public interface UserNotifRepo {

    List<UserNotifRecord> findAllNotificationsForUser(String username);

    void addUserNotification(UserNotif userNotif);

    UserNotifRecord updateUserNotif(UserNotif userNotif);

    UserNotifRecord findUserNotif(String username, Integer notifId);

    void updateUserNotifs(String username);

    int findNotSeenNotifs(String name);
}
