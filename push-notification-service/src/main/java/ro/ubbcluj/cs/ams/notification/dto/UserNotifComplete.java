package ro.ubbcluj.cs.ams.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserNotifComplete {

    private Integer id;
    private String courseId;
    private Integer postId;
    private String type;
    private String title;
    private String body;
    private LocalDateTime date;
}
