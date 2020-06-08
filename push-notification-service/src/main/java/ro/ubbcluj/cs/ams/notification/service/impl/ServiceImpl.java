package ro.ubbcluj.cs.ams.notification.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ro.ubbcluj.cs.ams.notification.dto.*;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.model.tables.records.NotificationRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionKeysRecord;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionRecord;
import ro.ubbcluj.cs.ams.notification.repository.notification.NotificationRepo;
import ro.ubbcluj.cs.ams.notification.repository.subsKeys.SubscriptionKeysRepo;
import ro.ubbcluj.cs.ams.notification.repository.subscription.SubscriptionRepo;
import ro.ubbcluj.cs.ams.notification.service.Service;

import java.util.List;
import java.util.Objects;

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
    private Mappers mappers;

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
    public Notification addNotification(Notification notification) {

        LOGGER.info("========== LOGGING addNotification ==========");

        NotificationRecord notificationRecord = notificationRepo.addNotification(notification);

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
    public SubscriptionKeys findSubscriptionKeysById(int id) {

        LOGGER.info("========== LOGGING findSubscriptionKeysById ==========");

        SubscriptionKeysRecord subscriptionKeysRecord=subscriptionKeysRepo.findById(id);

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscriptionKeysById ==========");
        return mappers.subscriptionKeysRecordToSubscriptionKeys(subscriptionKeysRecord);
    }
}
