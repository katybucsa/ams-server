package ro.ubbcluj.cs.ams.student.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HandleServicesHealthRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleServicesHealthRequests.class);

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

    @Value("${spring.application.name}")
    private String thisAppName;

    @Async
    public void sendResponseToService(String serviceName) {

        LOGGER.info("========== LOGGING sendResponseToServices ==========");
        LOGGER.info("========== send response to {} ==========", serviceName);
        String path = "http://" + serviceName + "/" + serviceName.split("[-]")[0] + "/present?service-name=" + thisAppName;
        webClientBuilder
                .build()
                .post()
                .uri(path)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
