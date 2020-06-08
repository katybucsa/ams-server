package ro.ubbcluj.cs.ams.notification.repository.subsKeys.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.notification.model.Tables;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionKeysRecord;
import ro.ubbcluj.cs.ams.notification.repository.subsKeys.SubscriptionKeysRepo;

@Repository
public class SubscriptionKeysRepoImpl implements SubscriptionKeysRepo {

    public static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionKeysRepoImpl.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public SubscriptionKeysRecord addSubscriptionKeys(SubscriptionKeys subscriptionKeys) {

        LOGGER.info("========== LOGGING addSubscriptionKeys ==========");

        SubscriptionKeysRecord subscriptionKeysRecord = dsl
                .insertInto(Tables.SUBSCRIPTION_KEYS, Tables.SUBSCRIPTION_KEYS.P256DH, Tables.SUBSCRIPTION_KEYS.AUTH)
                .values(subscriptionKeys.getP256dh(), subscriptionKeys.getAuth())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING addSubscriptionKeys ==========");
        return subscriptionKeysRecord;
    }

    @Override
    public SubscriptionKeysRecord findByP256dhAndAuth(String p256dh, String auth) {

        LOGGER.info("========== LOGGING findByP256dhAndAuth ==========");

        SubscriptionKeysRecord subscriptionKeysRecord = dsl
                .selectFrom(Tables.SUBSCRIPTION_KEYS)
                .where(Tables.SUBSCRIPTION_KEYS.P256DH.eq(p256dh))
                .and(Tables.SUBSCRIPTION_KEYS.AUTH.eq(auth))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findByP256dhAndAuth ==========");
        return subscriptionKeysRecord;
    }

    @Override
    public SubscriptionKeysRecord findById(int id) {

        LOGGER.info("========== LOGGING findById ==========");

        SubscriptionKeysRecord subscriptionKeysRecord = dsl
                .selectFrom(Tables.SUBSCRIPTION_KEYS)
                .where(Tables.SUBSCRIPTION_KEYS.ID.eq(id))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findById ==========");
        return subscriptionKeysRecord;
    }
}
