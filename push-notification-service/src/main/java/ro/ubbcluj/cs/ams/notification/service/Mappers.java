package ro.ubbcluj.cs.ams.notification.service;

import org.mapstruct.Mapper;
import ro.ubbcluj.cs.ams.notification.dto.subscription.SubscriptionDto;
import ro.ubbcluj.cs.ams.notification.dto.subscription.SubscriptionKeysDto;
import ro.ubbcluj.cs.ams.notification.dto.subscription.SubscriptionResponseDto;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.UserNotif;
import ro.ubbcluj.cs.ams.notification.model.tables.records.NotificationRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionKeysRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.UserNotifRecord;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Mappers {

    Subscription subscriptionDtoToSubscription(SubscriptionDto subscriptionDto);

    SubscriptionResponseDto subscriptionRecordToSubscriptionResponseDto(SubscriptionRecord subscriptionRecord);

    Notification notificationRecordToNotification(NotificationRecord notificationRecord);

    List<Subscription> subscriptionsRecordToSubscriptions(List<SubscriptionRecord> subscriptionRecords);

    SubscriptionKeys subscriptionKeysDtoToSubscriptionKeys(SubscriptionKeysDto keys);

    SubscriptionKeys subscriptionKeysRecordToSubscriptionKeys(SubscriptionKeysRecord subscriptionKeysRecord);

    UserNotif userNotifRecordToUserNotif(UserNotifRecord updateUserNotif);
}
