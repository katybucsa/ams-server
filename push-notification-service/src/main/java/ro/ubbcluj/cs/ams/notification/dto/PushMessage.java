package ro.ubbcluj.cs.ams.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class PushMessage {

    private final String title;

    private final String body;
}
