package ro.ubbcluj.cs.ams.notification.repository.subsKeys;

import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionKeysRecord;

public interface SubscriptionKeysRepo {

    SubscriptionKeysRecord addSubscriptionKeys(SubscriptionKeys subscriptionKeys);

    SubscriptionKeysRecord findByP256dhAndAuth(String p256dh, String auth);

    SubscriptionKeysRecord findById(int id);
}
