package ro.ubbcluj.cs.ams.auth.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ro.ubbcluj.cs.ams.utils.common.ServiceState;
import ro.ubbcluj.cs.ams.utils.config.EurekaServicesProperties;
import ro.ubbcluj.cs.ams.utils.config.TargetJarsProperties;
import ro.ubbcluj.cs.ams.utils.health.MicroserviceDetails;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ro.ubbcluj.cs.ams.utils.config.BeansConfig.queues;

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
    private Queue adminQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final List<Queue> QUEUES = new ArrayList<>(queues());

    public static final List<MicroserviceDetails> services = new ArrayList<>();
    private final List<String> aliveServices = new ArrayList<>();
    private List<MicroserviceDetails> detectedServices = new ArrayList<>();
    private final List<MicroserviceDetails> sentServices = new ArrayList<>();
    private static final long SEND_INTERVAL = 2000;
    private static boolean allStarted = false;

    @SneakyThrows
    @Scheduled(fixedDelay = SEND_INTERVAL)
    private void sendHeartBeatRequestToAllServices() {

        if (props.getNumber() == eurekaClient.getApplications().size() && !allStarted) {

            jmsTemplate.convertAndSend(adminQueue, objectMapper.writeValueAsString(ServiceState.builder()
                    .service(thisAppName.split("[-]")[0])
                    .state("running")
                    .build()));
            jmsTemplate.setReceiveTimeout(2000);
            jmsTemplate.receive(thisAppName.replace("service", "hqueue"));
            jmsTemplate.receive(thisAppName.replace("service", "rqueue"));
            allStarted = true;
            QUEUES.removeIf(q -> {
                try {
                    return q.getQueueName().split("[-]")[0].equals(thisAppName.split("[-]")[0]);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                return false;
            });
            addAllRegisteredServices();
        }
        if (allStarted) {

            LOGGER.info("========== all microservices running ==========");
            sentServices.clear();
            for (Queue queue : QUEUES) {
                String serviceName = queue.getQueueName().replace("hqueue", "service");
                MicroserviceDetails service = services.stream().filter(s -> s.getName().equals(serviceName)).findAny().get();
                if (!detectedServices.contains(service) ||
                        (detectedServices.contains(service) && service.isCalledMe())) {
                    jmsTemplate.convertAndSend(queue, thisAppName);
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
                    service.setTimesNoRun(2);
                    service.setCalledMe(false);
                    detectedServices.add(service);
                    if (iAmTheLeader()) {
                        LOGGER.info("========== I am the leader - {}", thisAppName);

                        jmsTemplate.convertAndSend(adminQueue, objectMapper.writeValueAsString(ServiceState.builder()
                                .service(service.getName().split("[-]")[0])
                                .state("error")
                                .build()));
                        Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"java -jar " + service.getJarPath() + "\"");
                    }
                }
            }
            Thread.sleep(4000);
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
                if (!serviceName.equals(thisAppName)) {
                    services.add(new MicroserviceDetails(serviceName, thisAppName, jarsProps));
                }
            });
        });
    }

    @SneakyThrows
    @JmsListener(destination = "auth-hqueue")
    public void listenerRequests(String serviceName) {

        LOGGER.info("========== Service request: {}", serviceName);

        jmsTemplate.convertAndSend(serviceName.replace("service", "rqueue"), thisAppName);
    }

    @SneakyThrows
    @JmsListener(destination = "auth-rqueue")
    public void listenerResponses(String serviceName) {

        LOGGER.info("========== Service alive: {}", serviceName);

        for (MicroserviceDetails service : services) {
            if (service.getName().equals(serviceName))
                service.setCalledMe(true);
        }
        aliveServices.add(serviceName);
    }
}
