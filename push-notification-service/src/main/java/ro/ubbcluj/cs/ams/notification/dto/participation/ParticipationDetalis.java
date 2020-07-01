package ro.ubbcluj.cs.ams.notification.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationDetalis {

    private String courseId;
    private String courseName;
    private int postId;
    private String postTitle;
    private String professorId;
    private List<String> participants;
}
