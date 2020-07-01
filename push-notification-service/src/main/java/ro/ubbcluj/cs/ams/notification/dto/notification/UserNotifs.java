package ro.ubbcluj.cs.ams.notification.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserNotifs {

    private List<UserNotifComplete> data;
}
