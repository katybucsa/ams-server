package ro.ubbcluj.cs.ams.notification.messageBroker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.notification.dto.participation.ParticipationDetalis;
import ro.ubbcluj.cs.ams.notification.dto.participation.PostResponseDto;
import ro.ubbcluj.cs.ams.notification.notificator.SendNotificationHandler;
import ro.ubbcluj.cs.ams.utils.common.ServiceState;

@Component
@EnableJms
public class MessageConsumer {

    @Autowired
    private SendNotificationHandler sendNotificationHandler;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @SneakyThrows
    @JmsListener(destination = "notification-queue")
    public void listener(String json) {

        LOGGER.info("========== Message received {} ==========", json);

        PostResponseDto postResponseDto = objectMapper.readValue(json, PostResponseDto.class);
        sendNotificationHandler.sendNotification(postResponseDto);
    }

    @SneakyThrows
    @JmsListener(destination = "particip-queue")
    public void listenerParticip(String json) {

        LOGGER.info("========== Message received {} ==========", json);

        ParticipationDetalis participationDetalis = objectMapper.readValue(json, ParticipationDetalis.class);
        sendNotificationHandler.sendParticipNotification(participationDetalis);
    }

    @SneakyThrows
    @JmsListener(destination = "admin-queue")
    public void listenerAdminQueue(String json) {

        LOGGER.info("========== Message received {} ==========", json);

        ServiceState serviceState = objectMapper.readValue(json, ServiceState.class);
        sendNotificationHandler.sendAdminNotification(serviceState);
    }
}
