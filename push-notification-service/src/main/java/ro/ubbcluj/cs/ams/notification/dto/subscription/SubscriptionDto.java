package ro.ubbcluj.cs.ams.notification.dto.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@ToString
public class SubscriptionDto {

    private String username;

    private String userRole;

    private final String endpoint;

    private final LocalDateTime expirationTime;

    private final SubscriptionKeysDto keys;

    @JsonCreator
    public SubscriptionDto(@JsonProperty("endpoint") String endpoint,
                           @JsonProperty("expirationTime") LocalDateTime expirationTime,
                           @JsonProperty("keys") SubscriptionKeysDto keys) {

        this.endpoint = endpoint;
        this.expirationTime = expirationTime;
        this.keys = keys;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getEndpoint() {
//
//        return this.endpoint;
//    }
//
//    public Long getExpirationTime() {
//
//        return this.expirationTime;
//    }
//
//    public SubscriptionKeys getKeys() {
//
//        return this.keys;
//    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        SubscriptionDto that = (SubscriptionDto) o;
//        return Objects.equals(username, that.username) &&
//                Objects.equals(endpoint, that.endpoint) &&
//                Objects.equals(expirationTime, that.expirationTime) &&
//                Objects.equals(keys, that.keys);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(username, endpoint, expirationTime, keys);
//    }
//
//    @Override
//    public String toString() {
//        return "Subscription{" +
//                "username='" + username + '\'' +
//                ", endpoint='" + endpoint + '\'' +
//                ", expirationTime=" + expirationTime +
//                ", keys=" + keys +
//                '}';
//    }
}
