package ro.ubbcluj.cs.ams.notification.notificator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.notification.dto.PostResponseDto;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionDto;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionEndpoint;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.service.CryptoService;
import ro.ubbcluj.cs.ams.notification.service.ServerKeys;
import ro.ubbcluj.cs.ams.notification.service.Service;
import rx.subscriptions.Subscriptions;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SendNotificationHandler {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Service service;

    @Autowired
    private PushNotificator pushNotificator;

    private final Map<Integer, SubscriptionKeys> localSubscriptionKeys = new ConcurrentHashMap<>();

    private final Map<Subscription, CompletableFuture<Boolean>> futures = new ConcurrentHashMap<>();

    private final static Logger LOGGER = LoggerFactory.getLogger(SendNotificationHandler.class);

    @SneakyThrows
    public void sendNotification(PostResponseDto postResponseDto) {

        Notification notification = new Notification();
        notification.setPostId(postResponseDto.getId());
        notification.setCourseId(postResponseDto.getCourseid());
        notification.setTitle(postResponseDto.getTitle());
        notification.setBody(postResponseDto.getText());
        Notification savedNotification = service.addNotification(notification);
        List<Subscription> subscriptions = service.findSubscriptionsByUserRole("STUDENT");
        sendPushMessageToAllSubscribers(subscriptions, savedNotification);
    }


    @SneakyThrows
    private void sendPushMessageToAllSubscribers(List<Subscription> subs,
                                                 Object message) throws JsonProcessingException {

        for (Subscription subscription : subs) {
            try {
                SubscriptionKeys keys = localSubscriptionKeys.get(subscription.getSubsId());
                if (Objects.isNull(keys)) {
                    keys = service.findSubscriptionKeysById(subscription.getSubsId());
                    localSubscriptionKeys.put(keys.getId(), keys);
                }

                byte[] result = this.cryptoService.encrypt(
                        this.objectMapper.writeValueAsString(message),
                        keys.getP256dh(), keys.getAuth(), 0);
                CompletableFuture<Boolean> remove = pushNotificator.sendPushMessage(subscription, result);
                futures.put(subscription, remove);
            } catch (InvalidKeyException | NoSuchAlgorithmException
                    | InvalidAlgorithmParameterException | IllegalStateException
                    | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException
                    | BadPaddingException e) {
                LOGGER.error("send encrypted message", e);
            }
        }

        for (Map.Entry<Subscription, CompletableFuture<Boolean>> cf : futures.entrySet()) {

            if (cf.getValue().get()) {
                LOGGER.info("========== failed to send push notification ==========");
                service.deleteSubscription(new SubscriptionEndpoint(cf.getKey().getEndpoint()));
            }
        }
    }
}