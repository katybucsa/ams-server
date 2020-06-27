package ro.ubbcluj.cs.ams.notification.service;

import org.springframework.scheduling.annotation.Async;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionDto;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionEndpoint;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionResponseDto;
import ro.ubbcluj.cs.ams.notification.dto.UserNotifs;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.UserNotif;

import java.util.List;

public interface Service {

    SubscriptionResponseDto addSubscription(SubscriptionDto subscriptionDto);

    void deleteSubscription(SubscriptionEndpoint subscriptionEndpoint);

    boolean existsSubscription(SubscriptionEndpoint subscriptionEndpoint);

    Notification addOrUpdateNotification(Notification notification);

    List<Subscription> findSubscriptionsByUserRole(String role);

    List<Subscription> findUsersSubscriptions(List<String> users);

    SubscriptionKeys findSubscriptionKeysById(int id);

    List<Subscription> findSubscriptionsByUserId(String professorId);

    UserNotifs findAllNotificationsForUser(String username);

    @Async
    void addUserNotification(String username, Integer notifId, Integer postId, String userRole);

    UserNotif updateUserNotification(UserNotif userNotif);

    int findNotSeenNotifications(String name);
}
