package ro.ubbcluj.cs.ams.course.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Event;

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

    private String date;

    private String type;

    private Event event;
}
