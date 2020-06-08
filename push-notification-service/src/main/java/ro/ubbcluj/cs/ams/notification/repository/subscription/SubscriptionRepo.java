package ro.ubbcluj.cs.ams.notification.repository.subscription;

import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.records.SubscriptionRecord;

import java.util.List;

public interface SubscriptionRepo {

    SubscriptionRecord addSubscription(Subscription subscription);

    SubscriptionRecord deleteSubscription(String subscriptionEndpoint);

    SubscriptionRecord findSubscription(String subscriptionEndpoint);

    List<SubscriptionRecord> findSubscriptionsByUserRole(String userRole);
}
