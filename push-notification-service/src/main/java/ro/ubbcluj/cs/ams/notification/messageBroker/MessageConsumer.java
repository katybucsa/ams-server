package ro.ubbcluj.cs.ams.notification.messageBroker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ro.ubbcluj.cs.ams.notification.dto.PostResponseDto;
import ro.ubbcluj.cs.ams.notification.notificator.SendNotificationHandler;

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

        LOGGER.info("========== Message received {}", json);

        PostResponseDto postResponseDto = objectMapper.readValue(json, PostResponseDto.class);
        sendNotificationHandler.sendNotification(postResponseDto);
    }
}
