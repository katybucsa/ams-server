package ro.ubbcluj.cs.ams.notification.controller;

import org.jooq.tools.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionDto;
import ro.ubbcluj.cs.ams.notification.dto.SubscriptionEndpoint;
import ro.ubbcluj.cs.ams.notification.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.notification.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.notification.model.tables.UserNotif;
import ro.ubbcluj.cs.ams.notification.notificator.SendNotificationHandler;
import ro.ubbcluj.cs.ams.notification.service.ServerKeys;
import ro.ubbcluj.cs.ams.notification.service.Service;

import java.security.Principal;

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
        System.out.println("=====");
        System.out.println(arr);
        return arr;
    }

    @GetMapping(path = "/signing-key")
    public String publicSigningKeyBase64() {

        String str = this.serverKeys.getPublicKeyBase64();
        System.out.println("=====");
        System.out.println(str);
        return str;
    }

    @PostMapping("/subscription")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(@RequestBody SubscriptionDto subscription, Principal principal) {

        LOGGER.info("========== LOGGING subscribe for user {} ==========", principal.getName());
        subscription.setUsername(principal.getName());
        subscription.setUserRole(((OAuth2Authentication) principal)
                .getUserAuthentication()
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority());

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

    @RequestMapping(value = "" ,method = RequestMethod.GET)
    public ResponseEntity<UserNotif> findNotificationsForUser(Principal principal){

        return null;
    }

//    @PostMapping("/isSubscribedAngular")
//    public boolean isSubscribedAngular(@RequestBody SubscriptionEndpoint subscription) {
//        return this.subscriptionsAngular.containsKey(subscription.getEndpoint());
//    }

//    @GetMapping(path = "/lastNumbersAPIFact")
//    public String lastNumbersAPIFact() {
//        return this.lastNumbersAPIFact;
//    }

//    @Scheduled(fixedDelay = 20_000)
//    public void numberFact() {
//        if (this.subscriptions.isEmpty()) {
//            return;
//        }
//
//        try {
//            HttpResponse<String> response = this.httpClient.send(
//                    HttpRequest.newBuilder(URI.create("http://numbersapi.com/random/date")).build(),
//                    BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                this.lastNumbersAPIFact = response.body();
//                sendPushMessageToAllSubscribersWithoutPayload();
//            }
//        } catch (IOException | InterruptedException e) {
//            LOGGER.error("fetch number fact", e);
//        }
//    }

//    @Scheduled(fixedDelay = 30_000)
//    public void chuckNorrisJoke() {
//        if (this.subscriptions.isEmpty() && this.subscriptionsAngular.isEmpty()) {
//            return;
//        }
//
//        try {
//            HttpResponse<String> response = this.httpClient.send(HttpRequest
//                            .newBuilder(URI.create("https://api.icndb.com/jokes/random")).build(),
//                    BodyHandlers.ofString());
//            if (response.statusCode() == 200) {
//                Map<String, Object> jokeJson = this.objectMapper.readValue(response.body(),
//                        Map.class);
//
//                @SuppressWarnings("unchecked")
//                Map<String, Object> value = (Map<String, Object>) jokeJson.get("value");
//                int id = (int) value.get("id");
//                String joke = (String) value.get("joke");
//
//                Notification notification = new Notification("Chuck Norris Joke: " + id);
//                notification.setBody(joke);
//                notification.setIcon("assets/chuck.png");
//
//                sendPushMessageToAllSubscribers(this.subscriptions,
//                        new PushMessage("Chuck Norris Joke: " + id, joke));
//
//
////                sendPushMessageToAllSubscribers(this.subscriptionsAngular,
////                        Map.of("notification", notification));
//            }
//        } catch (IOException | InterruptedException e) {
//            LOGGER.error("fetch chuck norris", e);
//        }
//    }

//    private void sendPushMessageToAllSubscribersWithoutPayload() {
//        Set<String> failedSubscriptions = new HashSet<>();
//        for (Subscription subscription : this.subscriptions.values()) {
//            boolean remove = sendPushMessage(subscription, null);
//            if (remove) {
//                failedSubscriptions.add(subscription.getEndpoint());
//            }
//        }
//        failedSubscriptions.forEach(this.subscriptions::remove);
//    }
//
//
}
