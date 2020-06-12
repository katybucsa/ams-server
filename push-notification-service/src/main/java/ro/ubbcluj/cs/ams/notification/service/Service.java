package ro.ubbcluj.cs.ams.notification.service;

import ro.ubbcluj.cs.ams.notification.dto.SubscriptionDto;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionEndpoint;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionResponseDto;
import ro.ubbcluj.cs.ams.notification.model.tables.UserNotif;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionRecord;

import java.util.List;

public interface Service {

    SubscriptionResponseDto addSubscription(SubscriptionDto subscriptionDto);

    void deleteSubscription(SubscriptionEndpoint subscriptionEndpoint);

    boolean existsSubscription(SubscriptionEndpoint subscriptionEndpoint);

    Notification addNotification(Notification notification);

    List<Subscription> findSubscriptionsByUserRole(String role);

    SubscriptionKeys findSubscriptionKeysById(int id);

    List<Subscription> findSubscriptionsByUserId(String professorId);

    List<UserNotif> findAllNotificationsForUser(String username);
}
