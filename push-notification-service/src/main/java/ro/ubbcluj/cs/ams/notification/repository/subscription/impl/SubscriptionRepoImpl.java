package ro.ubbcluj.cs.ams.notification.repository.subscription.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.notification.model.Tables;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionRecord;
import ro.ubbcluj.cs.ams.notification.repository.subscription.SubscriptionRepo;

import java.util.List;

@Repository
public class SubscriptionRepoImpl implements SubscriptionRepo {

    public static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRepoImpl.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public SubscriptionRecord addSubscription(Subscription subscription) {

        LOGGER.info("========== LOGGING addSubscription ==========");

        SubscriptionRecord subscriptionRecord = dsl.insertInto(Tables.SUBSCRIPTION, Tables.SUBSCRIPTION.USERNAME, Tables.SUBSCRIPTION.USER_ROLE, Tables.SUBSCRIPTION.ENDPOINT, Tables.SUBSCRIPTION.SUBS_ID)
                .values(subscription.getUsername(), subscription.getUserRole(), subscription.getEndpoint(), subscription.getSubsId())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING addSubscription ==========");
        return subscriptionRecord;
    }

    @Override
    public SubscriptionRecord deleteSubscription(String subscriptionEndpoint) {

        LOGGER.info("========== LOGGING deleteSubscription ==========");

        SubscriptionRecord subscriptionRecord = dsl.deleteFrom(Tables.SUBSCRIPTION)
                .where(Tables.SUBSCRIPTION.ENDPOINT.eq(subscriptionEndpoint))
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING deleteSubscription ==========");
        return subscriptionRecord;
    }

    @Override
    public SubscriptionRecord findSubscription(String subscriptionEndpoint) {

        LOGGER.info("========== LOGGING findSubscription ==========");

        SubscriptionRecord subscriptionRecord = dsl.selectFrom(Tables.SUBSCRIPTION)
                .where(Tables.SUBSCRIPTION.ENDPOINT.eq(subscriptionEndpoint))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscription ==========");
        return subscriptionRecord;
    }

    @Override
    public List<SubscriptionRecord> findSubscriptionsByUserRole(String userRole) {

        LOGGER.info("========== LOGGING findSubscription ==========");

        List<SubscriptionRecord> subscriptionRecords = dsl.selectFrom(Tables.SUBSCRIPTION)
                .where(Tables.SUBSCRIPTION.USER_ROLE.eq(userRole))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscription ==========");
        return subscriptionRecords;
    }
}