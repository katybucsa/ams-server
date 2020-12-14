package ro.ubbcluj.cs.ams.notification.controller;

import org.jooq.tools.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.cs.ams.notification.dto.notification.NotifNr;
import ro.ubbcluj.cs.ams.notification.dto.subscription.SubscriptionDto;
import ro.ubbcluj.cs.ams.notification.dto.subscription.SubscriptionEndpoint;
import ro.ubbcluj.cs.ams.notification.dto.notification.UserNotifs;
import ro.ubbcluj.cs.ams.notification.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.notification.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.UserNotif;
import ro.ubbcluj.cs.ams.notification.service.exception.NotificationExceptionType;
import ro.ubbcluj.cs.ams.notification.service.exception.NotificationServiceException;
import ro.ubbcluj.cs.ams.notification.service.impl.ServerKeys;
import ro.ubbcluj.cs.ams.notification.service.Service;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
public class NotificationController {

    @Autowired
    private ServicesHealthChecker servicesHealthChecker;

    @Autowired
    private HandleServicesHealthRequests handleServicesHealthRequests;

    @Autowired
    private ServerKeys serverKeys;

    @Autowired
    private Service service;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @RequestMapping(value = "/health", method = RequestMethod.POST, params = {"service-name"})
    public void health(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Health check from service: {} ", serviceName);

        handleServicesHealthRequests.sendResponseToService(serviceName);
    }

    @RequestMapping(value = "/present", method = RequestMethod.POST, params = {"service-name"})
    public void present(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Service {} is alive", serviceName);
        servicesHealthChecker.addService(serviceName);
    }

    @RequestMapping(value = "/running", method = RequestMethod.GET)
    public ResponseEntity running() {

        LOGGER.info("========== Service running ==========");
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
    public byte[] publicSigningKey() {

        byte[] arr = this.serverKeys.getPublicKeyUncompressed();
        return arr;
    }

    @GetMapping(path = "/signing-key")
    public String publicSigningKeyBase64() {

        String str = this.serverKeys.getPublicKeyBase64();
        return str;
    }

    @PostMapping("/subscription")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@RequestBody SubscriptionDto subscription, Principal principal) {

        LOGGER.info("========== LOGGING subscribe for user {} ==========", principal.getName());
        subscription.setUsername(principal.getName());
        System.out.println();
        subscription.setUserRole(((OAuth2Authentication) principal)
                .getAuthorities()
                .stream()
                .filter(x -> Character.isUpperCase(x.getAuthority().charAt(0)))
                .collect(Collectors.toList())
                .get(0).getAuthority());

        service.addSubscription(subscription);

        LOGGER.info("========== SUCCESSFULLY LOGGING subscribe for user {} ==========", principal.getName());
    }

    @PostMapping("/unsubscription")
    public void unsubscribe(@RequestBody SubscriptionEndpoint subscription, Principal principal) {

        LOGGER.info("========== LOGGING unsubscribe for user {} ==========", principal.getName());

        service.deleteSubscription(subscription);

        LOGGER.info("========== SUCCESSFULLY LOGGING unsubscribe for user {} ==========", principal.getName());
    }

    @PostMapping("/subscribed")
    public ResponseEntity<?> isSubscribed(@RequestBody SubscriptionEndpoint subscription, Principal principal) {

        LOGGER.info("========== LOGGING isSubscribed for user {} ==========", principal.getName());

        boolean existsSubsc = service.existsSubscription(subscription);

        LOGGER.info("========== User has subscription for provided endpoint {} ==========", existsSubsc);
        LOGGER.info("========== SUCCESSFULLY LOGGING isSubscribed for user {} ==========", principal.getName());
        return new ResponseEntity<>(new JSONObject().put("isSubscribed", existsSubsc), HttpStatus.OK);
    }

    @RequestMapping(value = "/notifs", method = RequestMethod.GET)
    public ResponseEntity<UserNotifs> findNotificationsForUser(Principal principal) {

        LOGGER.info("========== LOGGING findNotificationsForUser {} ==========", principal.getName());

        UserNotifs userNotifs = service.findAllNotificationsForUser(principal.getName());
        LOGGER.info("========== SUCCESSFULLY LOGGING findNotificationsForUser {} ==========", principal.getName());
        return new ResponseEntity<>(userNotifs, HttpStatus.OK);
    }

    @RequestMapping(value = "/notifs/nr", method = RequestMethod.GET)
    public ResponseEntity<NotifNr> findNotificationsNrForUser(Principal principal) {

        LOGGER.info("========== LOGGING findNotificationsNrForUser {} ==========", principal.getName());

        int nr = service.findNotSeenNotifications(principal.getName());

        NotifNr notifNr = NotifNr.builder().nr(nr).build();
        LOGGER.info("========== SUCCESSFULLY LOGGING findNotificationsNrForUser {} ==========", principal.getName());
        return new ResponseEntity<>(notifNr, HttpStatus.OK);
    }

    @RequestMapping(value = "/notifs", method = RequestMethod.POST)
    public ResponseEntity<UserNotif> updateUserNotification(@RequestBody UserNotif userNotif, Principal principal) {

        LOGGER.info("========== LOGGING updateUserNotification {} ==========", principal.getName());

        userNotif.setUserId(principal.getName());
        UserNotif updatedUserNotif = service.updateUserNotification(userNotif);

        LOGGER.info("========== SUCCESSFULLY LOGGING updateUserNotification {} ==========", principal.getName());
        return new ResponseEntity<>(updatedUserNotif, HttpStatus.OK);
    }

    @ExceptionHandler({NotificationServiceException.class})
    public ResponseEntity<NotificationExceptionType> handleException(NotificationServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }
}
