package ro.ubbcluj.cs.ams.course.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostResponseDto implements Serializable {

    private int id;

    private String title;

    private String text;

    private String courseid;
}
