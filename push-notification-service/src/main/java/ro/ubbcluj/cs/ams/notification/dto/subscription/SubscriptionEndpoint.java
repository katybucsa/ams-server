package ro.ubbcluj.cs.ams.notification.dto.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscriptionEndpoint {

    private final String endpoint;

    @JsonCreator
    public SubscriptionEndpoint(@JsonProperty("endpoint") String endpoint) {

        this.endpoint = endpoint;
    }

    public String getEndpoint() {

        return this.endpoint;
    }

    @Override
    public String toString() {

        return "SubscriptionEndpoint [endpoint=" + this.endpoint + "]";
    }

}
