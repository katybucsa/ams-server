package ro.ubbcluj.cs.ams.notification.dto.participation;

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
