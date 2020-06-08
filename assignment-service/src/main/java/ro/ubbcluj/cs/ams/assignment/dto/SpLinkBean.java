package ro.ubbcluj.cs.ams.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SpLinkBean implements Serializable {

    private String subjectId;

    private Integer activityTypeId;

    private String professorUsername;
}
