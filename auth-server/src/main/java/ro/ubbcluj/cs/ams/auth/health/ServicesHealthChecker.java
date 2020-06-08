package ro.ubbcluj.cs.ams.auth.health;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import ro.ubbcluj.cs.ams.utils.config.EurekaServicesProperties;
import ro.ubbcluj.cs.ams.utils.config.TargetJarsProperties;
import ro.ubbcluj.cs.ams.utils.health.MicroserviceDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class ServicesHealthChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesHealthChecker.class);

    @Autowired
    @Qualifier("eurekaClient")
    private EurekaClient eurekaClient;

    @Autowired
    private EurekaServicesProperties props;

    @Autowired
    private TargetJarsProperties jarsProps;

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

    @Value("${spring.application.name}")
    private String thisAppName;

    public static final List<MicroserviceDetails> services = new ArrayList<>();
    private final List<String> aliveServices = new ArrayList<>();
    private List<MicroserviceDetails> detectedServices = new ArrayList<>();
    private final List<MicroserviceDetails> sentServices = new ArrayList<>();
    private static final long SEND_INTERVAL = 2000;
    private static boolean allStarted = false;

    @SneakyThrows
    @Scheduled(fixedDelay = SEND_INTERVAL)
    private void sendHeartBeatRequestToAllServices() {

        while (true) {
            if (props.getNumber() == eurekaClient.getApplications().size() && !allStarted) {

                allStarted = true;
                addAllRegisteredServices();
                Thread.sleep(15000);
            }
            if (allStarted) {

                LOGGER.info("========== all microservices running ==========");
                sentServices.clear();
                services.forEach(service -> {
                    if (!detectedServices.contains(service) ||
                            (detectedServices.contains(service) && service.isCalledMe())) {

                        try {
                            webClientBuilder
                                    .build()
                                    .post()
                                    .uri(service.getHealthPath())
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .block();
                        } catch (Exception ignored) {

                        }
                        sentServices.add(service);
                        System.out.println(service.getName());
                    }
                });
                Thread.sleep(8000);
                verifyAliveServices();
            }
        }
    }

    @SneakyThrows
    public void verifyAliveServices() {

        detectedServices.forEach(service -> {
            service.setTimesNoRun(service.getTimesNoRun() - 1);
        });
        detectedServices = detectedServices.stream()
                .filter(service -> service.getTimesNoRun() != 0)
                .collect(Collectors.toList());
        if (allStarted) {
            System.out.println(aliveServices.size());
            System.out.println(sentServices.size());
            for (MicroserviceDetails service : sentServices) {
                if (!aliveServices.contains(service.getName())) {
                    service.setTimesNoRun(5);
                    service.setCalledMe(false);
                    detectedServices.add(service);
                    if (iAmTheLeader()) {
                        LOGGER.info("========== I am the leader - {}", thisAppName);
                        Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"java -jar " + service.getJarPath() + "\"");
                    }
                }
            }
            Thread.sleep(8000);
            aliveServices.clear();
        }
    }

    private boolean iAmTheLeader() {

        List<String> l = services.stream()
                .map(MicroserviceDetails::getName)
                .collect(Collectors.toList());
        l.add(thisAppName);
        return l.stream()
                .filter(serviceName -> aliveServices.contains(serviceName) || serviceName.equals(thisAppName))
                .sorted()
                .collect(Collectors.toList()).get(0).equals(thisAppName);
    }

    private void addAllRegisteredServices() {

        Applications applications = eurekaClient.getApplications();
        applications.getRegisteredApplications().forEach(registeredApplication -> {
            registeredApplication.getInstances().forEach(instance -> {
                String serviceName = instance.getInstanceId().split("[:]")[1];
                if (!serviceName.equals(thisAppName) && !serviceName.equals("gateway-service")) {
                    services.add(new MicroserviceDetails(serviceName, thisAppName, jarsProps));
                }
            });
        });
    }

    public void addService(String serviceName) {

        LOGGER.info("========== Adding service {} ==========", serviceName);
        for (MicroserviceDetails service : services) {
            if (service.getName().equals(serviceName))
                service.setCalledMe(true);
        }
        aliveServices.add(serviceName);
    }
}
