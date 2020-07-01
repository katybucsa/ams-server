package ro.ubbcluj.cs.ams.notification.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ro.ubbcluj.cs.ams.utils.common.ServiceState;
import ro.ubbcluj.cs.ams.utils.config.EurekaServicesProperties;
import ro.ubbcluj.cs.ams.utils.config.TargetJarsProperties;
import ro.ubbcluj.cs.ams.utils.health.MicroserviceDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@EnableJms
public class ServicesHealthCheckerAsync {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesHealthChecker.class);

    @Autowired
    @Qualifier("eurekaClient")
    private EurekaClient eurekaClient;

    @Autowired
    private EurekaServicesProperties props;

    @Autowired
    private TargetJarsProperties jarsProps;

    @Value("${spring.application.name}")
    private String thisAppName;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public static final List<MicroserviceDetails> services = new ArrayList<>();
    private final List<String> aliveServices = new ArrayList<>();
    private List<MicroserviceDetails> detectedServices = new ArrayList<>();
    private final List<MicroserviceDetails> sentServices = new ArrayList<>();
    private static final long SEND_INTERVAL = 100;
    private static boolean allStarted = false;

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() {

        jmsTemplate.convertAndSend("admin-queue", objectMapper.writeValueAsString(ServiceState.builder()
                .service(thisAppName.split("[-]")[0])
                .state("running")
                .build()));
    }

    @SneakyThrows
    @Scheduled(fixedDelay = SEND_INTERVAL)
    private void sendHeartBeatRequestToAllServices() {

        if (props.getNumber() == eurekaClient.getApplications().size() && !allStarted) {

            jmsTemplate.setReceiveTimeout(2000);
            jmsTemplate.receive(thisAppName.replace("service", "hqueue"));
            jmsTemplate.receive(thisAppName.replace("service", "rqueue"));

            allStarted = true;
            addAllRegisteredServices();
        }
        if (allStarted) {

            LOGGER.info("========== all microservices running ==========");
            sentServices.clear();
            for (MicroserviceDetails service : services) {
                String serviceQueue = service.getName().replace("service", "hqueue");
                if (!detectedServices.contains(service) ||
                        (detectedServices.contains(service) && service.isCalledMe())) {
                    jmsTemplate.convertAndSend(serviceQueue, thisAppName);
                    sentServices.add(service);
                    System.out.println(service.getName());
                }
            }
            Thread.sleep(6000);
            verifyAliveServices();
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
                    LOGGER.info("========== Service {} is down from {}", service.getName(),thisAppName);
                    service.setTimesNoRun(3);
                    service.setCalledMe(false);
                    detectedServices.add(service);
                    if (iAmTheLeader()) {
                        LOGGER.info("========== I am the leader - {} ==========", thisAppName);

                        Runtime.getRuntime().exec("cmd /c start /min cmd.exe /K \"java -Xverify:none -noverify -jar " + service.getJarPath() + "\"");
                    }
                }
            }
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

    @SneakyThrows
    @JmsListener(destination = "notification-hqueue")
    public void listenerRequests(String serviceName) {

        LOGGER.info("========== Service request: {}", serviceName);

        jmsTemplate.convertAndSend(serviceName.replace("service", "rqueue"), thisAppName);
    }

    @SneakyThrows
    @JmsListener(destination = "notification-rqueue")
    public void listenerResponses(String serviceName) {

        LOGGER.info("========== Service alive: {}", serviceName);

        for (MicroserviceDetails service : services) {
            if (service.getName().equals(serviceName))
                service.setCalledMe(true);
        }
        aliveServices.add(serviceName);
    }
}
