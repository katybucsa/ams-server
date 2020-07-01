package ro.ubbcluj.cs.ams.notification.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationAction {

    private String action;
    private String title;
    private String icon;
}
