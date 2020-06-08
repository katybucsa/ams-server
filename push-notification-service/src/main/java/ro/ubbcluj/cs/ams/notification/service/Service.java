package ro.ubbcluj.cs.ams.notification.service;

import ro.ubbcluj.cs.ams.notification.dto.NotificationResponseDto;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionDto;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionEndpoint;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionResponseDto;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;

import java.util.List;

public interface Service {

    SubscriptionResponseDto addSubscription(SubscriptionDto subscriptionDto);

    void deleteSubscription(SubscriptionEndpoint subscriptionEndpoint);

    boolean existsSubscription(SubscriptionEndpoint subscriptionEndpoint);

    Notification addNotification(Notification notification);

    List<Subscription> findSubscriptionsByUserRole(String role);

    SubscriptionKeys findSubscriptionKeysById(int id);
}
