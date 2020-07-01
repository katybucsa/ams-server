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

import static ro.ubbcluj.cs.ams.notification.model.tables.Subscription.SUBSCRIPTION;

@Repository
public class SubscriptionRepoImpl implements SubscriptionRepo {

    public static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRepoImpl.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public SubscriptionRecord addSubscription(Subscription subscription) {

        LOGGER.info("========== LOGGING addSubscription ==========");

        SubscriptionRecord subscriptionRecord = dsl.insertInto(SUBSCRIPTION, SUBSCRIPTION.USERNAME, SUBSCRIPTION.USER_ROLE, SUBSCRIPTION.ENDPOINT, SUBSCRIPTION.SUBS_ID)
                .values(subscription.getUsername(), subscription.getUserRole(), subscription.getEndpoint(), subscription.getSubsId())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING addSubscription ==========");
        return subscriptionRecord;
    }

    @Override
    public SubscriptionRecord deleteSubscription(String subscriptionEndpoint) {

        LOGGER.info("========== LOGGING deleteSubscription ==========");

        SubscriptionRecord subscriptionRecord = dsl.deleteFrom(SUBSCRIPTION)
                .where(SUBSCRIPTION.ENDPOINT.eq(subscriptionEndpoint))
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING deleteSubscription ==========");
        return subscriptionRecord;
    }

    @Override
    public SubscriptionRecord findSubscription(String subscriptionEndpoint) {

        LOGGER.info("========== LOGGING findSubscription ==========");

        SubscriptionRecord subscriptionRecord = dsl.selectFrom(SUBSCRIPTION)
                .where(SUBSCRIPTION.ENDPOINT.eq(subscriptionEndpoint))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscription ==========");
        return subscriptionRecord;
    }

    @Override
    public List<SubscriptionRecord> findSubscriptionsByUserRole(String userRole) {

        LOGGER.info("========== LOGGING findSubscription ==========");

        List<SubscriptionRecord> subscriptionRecords = dsl.selectFrom(SUBSCRIPTION)
                .where(SUBSCRIPTION.USER_ROLE.eq(userRole))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscription ==========");
        return subscriptionRecords;
    }

    @Override
    public List<SubscriptionRecord> findSubscriptionsByUserId(String professorId) {

        LOGGER.info("========== LOGGING findSubscriptionsByUserId ==========");

        List<SubscriptionRecord> subscriptionRecords = dsl.selectFrom(SUBSCRIPTION)
                .where(SUBSCRIPTION.USERNAME.eq(professorId))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findSubscriptionsByUserId ==========");
        return subscriptionRecords;
    }

    @Override
    public List<SubscriptionRecord> findUsersSubscriptions(List<String> users) {

        LOGGER.info("========== LOGGING findUsersSubscriptions ==========");

        List<SubscriptionRecord> subscriptionRecords = dsl.selectFrom(Tables.SUBSCRIPTION)
                .where(Tables.SUBSCRIPTION.USERNAME.in(users))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findUsersSubscriptions ==========");
        return subscriptionRecords;
    }
}
