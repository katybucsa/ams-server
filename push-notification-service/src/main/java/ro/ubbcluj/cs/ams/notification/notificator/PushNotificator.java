package ro.ubbcluj.cs.ams.notification.notificator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.notification.model.tables.pojos.Subscription;
import ro.ubbcluj.cs.ams.notification.service.impl.ServerKeys;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Component
public class PushNotificator {

    @Autowired
    private ServerKeys serverKeys;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private Algorithm jwtAlgorithm;

    private final static Logger LOGGER = LoggerFactory.getLogger(PushNotificator.class);

    @PostConstruct
    private void instantiateAlgorithm() {

        jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(),
                this.serverKeys.getPrivateKey());
    }

    @Async
    /**
     * @return true if the subscription is no longer valid and can be removed, false if
     * everything is okay
     */
    public CompletableFuture<Boolean> sendPushMessage(Subscription subscription, byte[] body) {
        String origin = null;
        try {
            URL url = new URL(subscription.getEndpoint());
            origin = url.getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            LOGGER.error("create origin", e);
            return CompletableFuture.completedFuture(true);
        }

        Date today = new Date();
        Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);

        String token = JWT.create().withAudience(origin).withExpiresAt(expires)
                .withSubject("ams-server-notificator").sign(this.jwtAlgorithm);

        URI endpointURI = URI.create(subscription.getEndpoint());

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        if (body != null) {
            httpRequestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Encoding", "aes128gcm");
        } else {
            httpRequestBuilder.POST(HttpRequest.BodyPublishers.noBody());
        }

        HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
                .header("Authorization",
                        "vapid t=" + token + ", k=" + this.serverKeys.getPublicKeyBase64())
                .build();
        try {
            HttpResponse<Void> response = this.httpClient.send(request,
                    HttpResponse.BodyHandlers.discarding());

            switch (response.statusCode()) {
                case 201:
                    LOGGER.info("Push message successfully sent: {}",
                            subscription.getEndpoint());
                    break;
                case 404:
                case 410:
                    LOGGER.warn("Subscription not found or gone: {}",
                            subscription.getEndpoint());
                    return CompletableFuture.completedFuture(true);
                case 429:
                    LOGGER.error("Too many requests: {}", request);
                    break;
                case 400:
                    LOGGER.error("Invalid request: {}", request);
                    break;
                case 413:
                    LOGGER.error("Payload size too large: {}", request);
                    break;
                default:
                    LOGGER.error("Unhandled status code: {} / {}", response.statusCode(),
                            request);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("send push message", e);
        }

        return CompletableFuture.completedFuture(false);
    }
}
