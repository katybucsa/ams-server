package ro.ubbcluj.cs.ams.notification.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AdminNotif {

    private String service;
    private String state;
    private String title;
    private String body;
    private String to = "admin";
}
