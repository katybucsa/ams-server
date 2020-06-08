package ro.ubbcluj.cs.ams.notification.repository.notification;

import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.records.NotificationRecord;

public interface NotificationRepo {

    NotificationRecord addNotification(Notification notification);

}
