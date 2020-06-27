package ro.ubbcluj.cs.ams.notification.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ro.ubbcluj.cs.ams.notification.dto.*;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.UserNotif;
import ro.ubbcluj.cs.ams.notification.model.tables.records.NotificationRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionKeysRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.UserNotifRecord;
import ro.ubbcluj.cs.ams.notification.repository.notification.NotificationRepo;
import ro.ubbcluj.cs.ams.notification.repository.notification.userNotif.UserNotifRepo;
import ro.ubbcluj.cs.ams.notification.repository.subsKeys.SubscriptionKeysRepo;
import ro.ubbcluj.cs.ams.notification.repository.subscription.SubscriptionRepo;
import ro.ubbcluj.cs.ams.notification.service.Service;

import javax.inject.Provider;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceImpl.class);

    @Autowired
    private SubscriptionRepo subscriptionRepo;

    @Autowired
    private SubscriptionKeysRepo subscriptionKeysRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserNotifRepo userNotifRepo;

    @Autowired
    private Mappers mappers;

    @Autowired
    private Provider<Instant> provider;

    @Override
    public SubscriptionResponseDto addSubscription(SubscriptionDto subscriptionDto) {

        LOGGER.info("========== LOGGING addSubscription ==========");

        SubscriptionKeysRecord subscriptionKeysRecord = subscriptionKeysRepo
                .findByP256dhAndAuth(subscriptionDto.getKeys().getP256dh(),
                        subscriptionDto.getKeys().getAuth());
        if (Objects.isNull(subscriptionKeysRecord)) {
            subscriptionKeysRecord = subscriptionKeysRepo
                    .addSubscriptionKeys(mappers.subscriptionKeysDtoToSubscriptionKeys(subscriptionDto.getKeys()));
        }
        Subscription subscription = mappers.subscriptionDtoToSubscription(subscriptionDto);
        subscription.setSubsId(subscriptionKeysRecord.getId());

        SubscriptionRecord subscriptionRecord = subscriptionRepo
                .addSubscription(subscription);

        LOGGER.info("========== SUCCESSFULLY LOGGING addSubscription ==========");
        return mappers.subscriptionRecordToSubscriptionResponseDto(subscriptionRecord);
    }

    @Override
    public void deleteSubscription(SubscriptionEndpoint subscriptionEndpoint) {

        LOGGER.info("========== LOGGING deleteSubscription ==========");

        SubscriptionRecord subscriptionRecord = subscriptionRepo
                .deleteSubscription(subscriptionEndpoint.getEndpoint());
        if (Objects.isNull(subscriptionRecord))
            LOGGER.info("========== No subscription for the provided endpoint found ==========");
        else
            LOGGER.info("========== Subscription deleted successfully ==========");
        LOGGER.info("========== SUCCESSFULLY LOGGING deleteSubscription ==========");
    }

    @Override
    public boolean existsSubscription(SubscriptionEndpoint subscriptionEndpoint) {

        LOGGER.info("========== LOGGING existsSubscription ==========");

        SubscriptionRecord subscriptionRecord = subscriptionRepo
                .findSubscription(subscriptionEndpoint.getEndpoint());

        LOGGER.info("========== SUCCESSFULLY LOGGING existsSubscription ==========");
        return !Objects.isNull(subscriptionRecord);
    }

    @Override
    public Notification addOrUpdateNotification(Notification notification) {

        LOGGER.info("========== LOGGING addNotification ==========");

        NotificationRecord notificationExistent = notificationRepo.findNotification(notification.getPostId(), notification.getType());
        NotificationRecord notificationRecord;
        notification.setDate(LocalDateTime.ofInstant(provider.get(), ZoneId.systemDefault()));
        if (!Objects.isNull(notificationExistent)) {
            notification.setId(notificationExistent.getId());
            notificationRecord = notificationRepo.updateNotification(notification);
        } else
            notificationRecord = notificationRepo.addNotification(notification);

        LOGGER.info("========== SUCCESSFULLY LOGGING addNotification ==========");
        return mappers.notificationRecordToNotification(notificationRecord);
    }

    @Override
    public List<Subscription> findSubscriptionsByUserRole(String role) {

        LOGGER.info("========== LOGGING findSubscriptionsByUserRole ==========");

        List<SubscriptionRecord> subscriptionRecords = subscriptionRepo.findSubscriptionsByUserRole(role);

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscriptionsByUserRole ==========");
        return mappers.subscriptionsRecordToSubscriptions(subscriptionRecords);

    }

    @Override
    public List<Subscription> findUsersSubscriptions(List<String> users) {

        LOGGER.info("========== LOGGING findUsersSubscriptions ==========");

        List<SubscriptionRecord> subscriptionRecords = subscriptionRepo.findUsersSubscriptions(users);

        LOGGER.info("========== SUCCESSFULLY LOGGING findUsersSubscriptions ==========");
        return mappers.subscriptionsRecordToSubscriptions(subscriptionRecords);
    }

    @Override
    public SubscriptionKeys findSubscriptionKeysById(int id) {

        LOGGER.info("========== LOGGING findSubscriptionKeysById ==========");

        SubscriptionKeysRecord subscriptionKeysRecord = subscriptionKeysRepo.findById(id);

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscriptionKeysById ==========");
        return mappers.subscriptionKeysRecordToSubscriptionKeys(subscriptionKeysRecord);
    }

    @Override
    public List<Subscription> findSubscriptionsByUserId(String professorId) {

        LOGGER.info("========== LOGGING findSubscriptionsByUserId ==========");

        List<SubscriptionRecord> subscriptionRecords = subscriptionRepo.findSubscriptionsByUserId(professorId);

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscriptionsByUserId ==========");
        return mappers.subscriptionsRecordToSubscriptions(subscriptionRecords);
    }

    @Override
    public UserNotifs findAllNotificationsForUser(String username) {

        LOGGER.info("========== LOGGING findAllNotificationsForUser ==========");

        userNotifRepo.updateUserNotifs(username);
        List<UserNotifRecord> userNotifRecords = userNotifRepo.findAllNotificationsForUser(username);
        List<UserNotifComplete> userNotifCompletes = new ArrayList<>();
        userNotifRecords.forEach(notif -> {

            NotificationRecord notificationRecord = notificationRepo.findNotification(notif.getNotifId());
            UserNotifComplete complete = UserNotifComplete.builder()
                    .id(notif.getNotifId())
                    .courseId(notificationRecord.getCourseId())
                    .postId(notificationRecord.getPostId())
                    .type(notificationRecord.getType())
                    .title(notificationRecord.getTitle())
                    .body(notificationRecord.getBody())
                    .date(notificationRecord.getDate())
                    .build();
            userNotifCompletes.add(complete);
        });

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllNotificationsForUser ==========");
        return UserNotifs.builder()
                .data(userNotifCompletes
                        .stream()
                        .sorted(Comparator.comparing(UserNotifComplete::getDate).reversed())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Async
    public void addUserNotification(String username, Integer notifId, Integer postId, String userRole) {

        LOGGER.info("========== LOGGING addUserNotification ==========");

        UserNotifRecord userNotifRecord = userNotifRepo.findUserNotif(username, notifId);
        if (!Objects.isNull(userNotifRecord))
            return;
        UserNotif userNotif = new UserNotif(username, notifId, postId, userRole, false, false);
        userNotifRepo.addUserNotification(userNotif);

        LOGGER.info("========== SUCCESSFULLY LOGGING addUserNotification ==========");
    }

    @Override
    public UserNotif updateUserNotification(UserNotif userNotif) {

        LOGGER.info("========== LOGGING updateUserNotification ==========");

        UserNotif updatedUserNotif = mappers.userNotifRecordToUserNotif(userNotifRepo.updateUserNotif(userNotif));

        LOGGER.info("========== SUCCESSFULLY LOGGING updateUserNotification ==========");
        return updatedUserNotif;
    }

    @Override
    public int findNotSeenNotifications(String name) {

        LOGGER.info("========== LOGGING findNotSeenNotifications ==========");

        int nr = userNotifRepo.findNotSeenNotifs(name);

        LOGGER.info("========== SUCCESSFULLY LOGGING findNotSeenNotifications ==========");
        return nr;
    }
}
