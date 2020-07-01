package ro.ubbcluj.cs.ams.notification.notificator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ro.ubbcluj.cs.ams.notification.dto.participation.AdminNotif;
import ro.ubbcluj.cs.ams.notification.dto.participation.EnrolledStudents;
import ro.ubbcluj.cs.ams.notification.dto.participation.ParticipationDetalis;
import ro.ubbcluj.cs.ams.notification.dto.participation.PostResponseDto;
import ro.ubbcluj.cs.ams.notification.dto.subscription.SubscriptionEndpoint;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Notification;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.SubscriptionKeys;
import ro.ubbcluj.cs.ams.notification.service.exception.NotificationExceptionType;
import ro.ubbcluj.cs.ams.notification.service.exception.NotificationServiceException;
import ro.ubbcluj.cs.ams.notification.service.impl.CryptoService;
import ro.ubbcluj.cs.ams.notification.service.Service;
import ro.ubbcluj.cs.ams.utils.common.ServiceState;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

    private final Map<Integer, SubscriptionKeys> localSubscriptionKeys = new ConcurrentHashMap<>();

    private final Map<Subscription, CompletableFuture<Boolean>> futures = new ConcurrentHashMap<>();

    private final static Logger LOGGER = LoggerFactory.getLogger(SendNotificationHandler.class);

    @SneakyThrows
    @Async
    public void sendNotification(PostResponseDto postResponseDto) {

        Notification notification = new Notification();
        notification.setPostId(postResponseDto.getId());
        notification.setCourseId(postResponseDto.getCourseId());
        notification.setType("new_post");
        notification.setTitle("New post from course " + postResponseDto.getCourseName() + ": " + postResponseDto.getTitle());
        notification.setBody(postResponseDto.getText());
        Notification savedNotification = service.addOrUpdateNotification(notification);
        List<String> enrolledStudents = findAllEnrolledStudents(postResponseDto.getCourseId());
        List<Subscription> subscriptions;
        if (enrolledStudents.size() == 0) {
            subscriptions = new ArrayList<>();
        } else
            subscriptions = service.findUsersSubscriptions(enrolledStudents);
        enrolledStudents.forEach(student->{
            service.addUserNotification(student,savedNotification.getId(),savedNotification.getPostId(),"STUDENT");
        });
        sendPushMessageToAllSubscribers(subscriptions, savedNotification);
    }

    @SneakyThrows
    @Async
    public void sendParticipNotification(ParticipationDetalis participationDetalis) {

        List<Subscription> subscriptions = service.findSubscriptionsByUserId(participationDetalis.getProfessorId());
        String body;
        if (participationDetalis.getParticipants().size() > 2) {
            body = participationDetalis.getParticipants().get(0) +
                    ", " + participationDetalis.getParticipants().get(1) +
                    " another " + (participationDetalis.getParticipants().size() - 2) + " participate to " +
                    participationDetalis.getPostTitle();
        } else {
            if (participationDetalis.getParticipants().size() == 1) {
                body = participationDetalis.getParticipants().get(0) +
                        " participates to " +
                        participationDetalis.getPostTitle();
            } else {
                body = participationDetalis.getParticipants().get(0) +
                        ", " + participationDetalis.getParticipants().get(1) +
                        " participate to " +
                        participationDetalis.getPostTitle();
            }
        }
        Notification notification = new Notification();
        notification.setPostId(participationDetalis.getPostId());
        notification.setCourseId(participationDetalis.getCourseId());
        notification.setType("new_particip");
        notification.setTitle(participationDetalis.getCourseName());
        notification.setBody(body);
        Notification savedNotification = service.addOrUpdateNotification(notification);
        service.addUserNotification(participationDetalis.getProfessorId(),savedNotification.getId(),savedNotification.getPostId(),"PROFESSOR");
        sendPushMessageToAllSubscribers(subscriptions, savedNotification);
    }

    @SneakyThrows
    @Async
    public void sendAdminNotification(ServiceState serviceState) {

        List<Subscription> subscriptions = service.findSubscriptionsByUserRole("ADMIN");
        AdminNotif notification = AdminNotif.builder()
                .service(serviceState.getService())
                .state(serviceState.getState())
                .to("admin")
                .build();
        if (serviceState.getState().equals("running")) {
            notification.setTitle("Service running again");
            notification.setBody("Service " + serviceState.getService().split("[-]")[0].toUpperCase() + " is running");
        } else {
            notification.setTitle("Service stopped running");
            notification.setBody("Service " + serviceState.getService().split("[-]")[0].toUpperCase() + " has stopped");
        }

        sendPushMessageToAllSubscribers(subscriptions, notification);
    }

    @SneakyThrows
    private void sendPushMessageToAllSubscribers(List<Subscription> subs,
                                                 Object message) {

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

    @Retryable(value = {WebClientResponseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 6000))
    @Cacheable(value = "enrolledStudents", key = "#courseId")
    public List<String> findAllEnrolledStudents(String courseId) {

        return Objects.requireNonNull(webClientBuilder
                .build()
                .get()
                .uri("http://student-service/student/enrollment?courseId=" + courseId)
                .retrieve()
                .bodyToMono(EnrolledStudents.class)
                .block())
                .getData();
    }

    @Recover
    public List<String> recover(WebClientResponseException t) {

        LOGGER.info("Recover");
        throw new NotificationServiceException("Could not get enrolled students!", NotificationExceptionType.ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
