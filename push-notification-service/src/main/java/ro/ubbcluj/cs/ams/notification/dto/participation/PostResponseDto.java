package ro.ubbcluj.cs.ams.notification.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostResponseDto implements Serializable {

    private int id;

    private String title;

    private String text;

    private String courseId;

    private String courseName;

    private LocalDateTime date;

    private String type;

    private Event event;
}
